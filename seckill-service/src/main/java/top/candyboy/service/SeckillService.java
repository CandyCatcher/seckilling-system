package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.SeckillOrder;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.ItemKey;
import top.candyboy.redis.key.SeckillKey;
import top.candyboy.util.MD5Util;
import top.candyboy.util.UUIDUtil;
import top.candyboy.pojo.vo.ItemVo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {
    private static char[] ops = new char[]{'+', '-', '*'};

    ItemService itemService;
    OrderService orderService;
    RedisService redisService;
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }
    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    //进行秒杀减库存
    @Transactional
    public OrderInfo doSeckill(User user, ItemVo itemVo) {
        //减库存
        boolean reduceStock = itemService.reduceStock(itemVo);
        if (reduceStock) {
            //下订单
            return orderService.createOrder(user, itemVo);
        } else {
            //没库存了，将商品id加载到库存中
            setItemOver(itemVo.getId());
            return null;
        }
    }

    public long getSeckillResult(Long userId, Long itemId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdItemId(userId, itemId);
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        } else {
            //没有秒杀到，然后有两种情况：没库存，还在队列中
            boolean itemOver = getItemOver(itemId);
            if (itemOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setItemOver(Long itemId) {
        redisService.set(ItemKey.stockOver, "" + itemId, true);
    }

    private boolean getItemOver(Long itemId) {
        return redisService.exist(ItemKey.stockOver, "" + itemId);
    }

    public boolean checkPath(User user, Long itemId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String s = redisService.get(SeckillKey.getPath, user.getId() + "_" + itemId, String.class);
        return path.equals(s);
    }

    public String createSeckillPath(User user, Long itemId) {
        String uuid = MD5Util.md5(UUIDUtil.uuid());
        redisService.set(SeckillKey.getPath, user.getId() + "_" + itemId, uuid);
        return uuid;
    }

    public BufferedImage createVerifyCodeImg(User user, Long itemId) {

        if (user == null && itemId == null) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //在这上面画东西
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        // 生成了50个像素
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+"_"+itemId, rnd);
        //输出图片
        return image;
    }

    //计算表达式结果
    private int calc(String exp) {
        try {
            //script引擎
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");
            return (int) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        return "" + num1 + op1 + num2 + op2 + num3;
    }

    public boolean checkVerifyCode(User user, Long itemId, int verifyCode) {
        if (user == null && itemId <= 0) {
            return false;
        }
        Integer code = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId() + "_" + itemId, Integer.class);
        if (code == null || code - verifyCode != 0) {
            return false;
        }
        //否则下一次用还是旧的
        redisService.del(SeckillKey.getSeckillVerifyCode, user.getId()+","+itemId);
        return true;
    }
}