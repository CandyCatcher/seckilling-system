package top.candyboy.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.candyboy.pojo.SeckillOrder;
import top.candyboy.pojo.User;
import top.candyboy.rabbitmq.MQSender;
import top.candyboy.rabbitmq.SeckillMessage;
import top.candyboy.redis.key.ItemKey;
import top.candyboy.redis.RedisOperation;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.ItemService;
import top.candyboy.service.OrderService;
import top.candyboy.service.SeckillService;
import top.candyboy.pojo.vo.ItemVo;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口的优化：
 * redis预减库存减少数据库访问
 * 内存标记减少redis访问
 * 请求先入队缓冲，异步下单，增强用户体验
 * RabbitMQ消息队列
 * Nginx水平扩展
 * 数据库分库分表---中间件mycat
 *
 * 核心思路：减少数据库访问
 * 1.系统初始化，把商品库存数量加载到Redis
 * 2.收到请求，Redis预减库存，库存不足，直接返回
 * 3.请求入队，放到队列中，也不是直接下单的，返回一个排队中
 * 4.请求出队，生成订单，减少库存
 * 5.客户端轮询，是否秒杀成功
 */
@Controller
public class SeckillController implements InitializingBean {

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    OrderService orderService;
    ItemService ItemService;
    SeckillService seckillService;
    RedisOperation redisOperation;
    MQSender mqSender;

    @Autowired
    public void setItemService(ItemService ItemService) {
        this.ItemService = ItemService;
    }
    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    @Autowired
    public void setSeckillService(SeckillService seckillService) {
        this.seckillService = seckillService;
    }
    @Autowired
    public void setRedisOperation(RedisOperation redisOperation) {
        this.redisOperation = redisOperation;
    }
    @Autowired
    public void setMqSender(MQSender mqSender) {
        this.mqSender = mqSender;
    }

    /**
     * 系统初始化的时候，就直接加载秒杀的商品放到缓存中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<ItemVo> listItemVO = ItemService.getListItemVO();
        if (listItemVO == null) {
            return;
        }
        for (ItemVo ItemVo : listItemVO) {
            redisOperation.set(ItemKey.getItemStock, "" + ItemVo.getId(), ItemVo.getStockCount());
            localOverMap.put(ItemVo.getId(), false);
        }
    }

    /*
    get和post真正的区别
    get是密等的 不会对服务端产生影响
    post相反
     */
    @RequestMapping(value = "/doseckill/{path}", method = RequestMethod.POST)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("ItemId")Long ItemId) {
    public Result<Integer> secSkill(User user, @RequestParam("ItemId")Long ItemId, @PathVariable("path") String path) {

        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //验证一下path
        boolean check = seckillService.checkPath(user, ItemId, path);
        if (!check) {
            return Result.error(CodeMsg.PATH_ILLEGEAL);
        }

        /*
        先判断库存还有没有

        怎么解决超卖的问题
        10个商品，同一个用户同时发出了两个请求，就会出现一个用户秒杀了两个商品
        在更新数据库库存时加一个判断
        最有效的就是利用数据库的唯一索引
         */

        /*
        预减库存
         */
        Boolean over = localOverMap.get(ItemId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 减少缓存的库存数量，返回的是减完的数量
        Long stock = redisOperation.decr(ItemKey.getItemStock, "" + ItemId);

        if (stock < 0) {
            //一旦变为false就不需要访问redis了
            localOverMap.put(ItemId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        /*
        第二步判断是否秒杀到了
        通过看有没有生成订单判断是否秒杀到了，在这里解决超卖的问题
        并且不能重复秒杀
         */
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdItemId(user.getId(), ItemId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        /*
        第三步进行排队 同步下单改为异步下单
         */
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setItemId(ItemId);
        mqSender.sendMessage(message);
        return Result.success(0);

        /*
        // 判断库存 10个商品，一个用户同事发出两个请求req1 req2，那么就可以同时减库存了
        // 解决：加一个唯一索引
        // 这里在大并发的时候会出现负库存的情况
        ItemVo ItemVo = ItemService.getItemVoById(ItemId);

        Integer stockCount = ItemVo.getStockCount();
        if (stockCount<= 0) {
            return Result.error(CodeMsg.SECKILLING_OVER);
        }
        //判断是否秒杀到了，是为了防止一个人秒杀到了多个商品，进入order中来查询
        SeckillingOrder seckillingOrder = orderService.getSeckillingOrderByUserIdItemId(user.getId(), ItemId);
        if (seckillingOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILLING);
        }
        //下订单并写入秒杀订单
        OrderInfo orderInfo = seckillingService.seckilling(user, ItemVo);
        System.out.println(orderInfo.toString());

        return Result.success(orderInfo);
        */
    }

    /*
    进入到队列之后，客户端会一直轮询
     */
    @GetMapping(value = "/getSeckillResult")
    public Result<Long> secSkillResult(User user, @RequestParam("ItemId")Long ItemId) {

        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        /*
        获取状态
        orderId: 秒杀成功
        -1: 秒杀失败
        0: 排队成功
         */
        Long result = seckillService.getSeckillResult(user.getId(), ItemId);
        return Result.success(result);
    }

    /*
    获取秒杀链接
     */
    @RequestMapping(value = "/getSeckillPath", method = RequestMethod.GET)
    public Result<String> getSeckillPath(User user, @RequestParam("ItemId")Long ItemId, @RequestParam("verifyCode")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        boolean checkVerifyCode = seckillService.checkVerifyCode(user, ItemId, verifyCode);
        if (!checkVerifyCode) {
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String seckillPath = seckillService.createSeckillPath(user, ItemId);
        return Result.success(seckillPath);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("ItemId")Long ItemId) {
    public Result<String> getVerifyCode(User user, HttpServletResponse response, @RequestParam("ItemId")Long ItemId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        BufferedImage verifyCodeImg = seckillService.createVerifyCodeImg(user, ItemId);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(verifyCodeImg, "png", outputStream);
            outputStream.flush();;
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
        return null;
    }

}
