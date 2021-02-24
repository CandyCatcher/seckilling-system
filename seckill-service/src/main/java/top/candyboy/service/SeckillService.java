package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.SeckillOrder;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.CommodityKey;
import top.candyboy.redis.key.SeckillKey;
import top.candyboy.util.MD5Util;
import top.candyboy.util.UUIDUtil;
import top.candyboy.pojo.vo.CommodityVo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {
    private static char[] ops = new char[]{'+', '-', '*'};

    CommodityService commodityService;
    OrderService orderService;
    RedisService redisService;
    @Autowired
    public void setCommodityService(CommodityService commodityService) {
        this.commodityService = commodityService;
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
    public OrderInfo doSeckill(User user, CommodityVo commodityVo) {
        //减库存
        boolean reduceStock = commodityService.reduceStock(commodityVo);
        if (reduceStock) {
            //下订单
            return orderService.createOrder(user, commodityVo);
        } else {
            //没库存了，将商品id加载到库存中
            setCommodityOver(commodityVo.getId());
            return null;
        }
    }

    public long getSeckillResult(Long userId, Long commodityId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdCommodityId(userId, commodityId);
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        } else {
            //没有秒杀到，然后有两种情况：没库存，还在队列中
            boolean commodityOver = getCommodityOver(commodityId);
            if (commodityOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setCommodityOver(Long commodityId) {
        redisService.set(CommodityKey.stockOver, "" + commodityId, true);
    }

    private boolean getCommodityOver(Long commodityId) {
        return redisService.exist(CommodityKey.stockOver, "" + commodityId);
    }

    public boolean checkPath(User user, Long commodityId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String s = redisService.get(SeckillKey.getPath, user.getId() + "_" + commodityId, String.class);
        return path.equals(s);
    }

    public String createSeckillPath(User user, Long commodityId) {
        String uuid = MD5Util.md5(UUIDUtil.uuid());
        redisService.set(SeckillKey.getPath, user.getId() + "_" + commodityId, uuid);
        return uuid;
    }

    public BufferedImage createVerifyCodeImg(User user, Long commodityId) {

        if (user == null && commodityId == null) {
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
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+"_"+commodityId, rnd);
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

    public boolean checkVerifyCode(User user, Long commodityId, int verifyCode) {
        if (user == null && commodityId <= 0) {
            return false;
        }
        Integer code = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId() + "_" + commodityId, Integer.class);
        if (code == null || code - verifyCode != 0) {
            return false;
        }
        //否则下一次用还是旧的
        redisService.del(SeckillKey.getSeckillVerifyCode, user.getId()+","+commodityId);
        return true;
    }
}