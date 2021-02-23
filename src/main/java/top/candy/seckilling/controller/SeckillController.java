package top.candy.seckilling.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.candy.seckilling.pojo.SeckillOrder;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.rabbitmq.MQSender;
import top.candy.seckilling.rabbitmq.SeckillMessage;
import top.candy.seckilling.redis.CommodityKey;
import top.candy.seckilling.redis.RedisService;
import top.candy.seckilling.redis.SeckillKey;
import top.candy.seckilling.result.CodeMsg;
import top.candy.seckilling.result.Result;
import top.candy.seckilling.service.CommodityService;
import top.candy.seckilling.service.OrderService;
import top.candy.seckilling.service.SeckillService;
import top.candy.seckilling.util.MD5Util;
import top.candy.seckilling.util.UUIDUtil;
import top.candy.seckilling.vo.CommodityVo;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
 * 3.请求如果，立即返回排队中
 * 4.请求出队，生成订单，减少库存
 * 5.客户端轮训，是否秒杀成功
 */
@Controller
public class SeckillController implements InitializingBean {

    private Map<Long, Boolean> longOverMap = new HashMap<>();

    OrderService orderService;
    CommodityService commodityService;
    SeckillService seckillService;
    RedisService redisService;
    MQSender mqSender;

    @Autowired
    public void setCommodityService(CommodityService commodityService) {
        this.commodityService = commodityService;
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
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }
    @Autowired
    public void setMqSender(MQSender mqSender) {
        this.mqSender = mqSender;
    }

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<CommodityVo> listCommodityVO = commodityService.getListCommodityVO();
        if (listCommodityVO == null) {
            return;
        }
        for (CommodityVo commodityVo:listCommodityVO) {
            redisService.set(CommodityKey.getCommodityId, "" + commodityVo.getId(), commodityVo.getStockCount());
            //System.out.println("stock count  " + commodityVo.getId() + " + " + commodityVo.getStockCount());
            longOverMap.put(commodityVo.getId(), false);
        }
    }

    /*
    get和post真正的区别
    get是密等的 不会对服务端产生影响
    post相反
     */
    @RequestMapping(value = "/do_seckill/{path}", method = RequestMethod.POST)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("commodityId")Long commodityId) {
    public Result<Integer> secSkill(User user, @RequestParam("commodityId")Long commodityId, @PathVariable("path") String path) {

        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //验证一下path
        boolean check = seckillService.checkPath(user, commodityId, path);
        if (!check) {
            return Result.error(CodeMsg.PATH_ILLEGEAL);
        }
        //第一步将库存加载到缓存中
        //这一步也是可以优化，之后的就不用访问redis了，使用一个内存标记
        Boolean over = longOverMap.get(commodityId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        Long stock = redisService.decr(CommodityKey.getCommodityId, "" + commodityId);
        //System.out.println(stock);
        if (stock < 0) {
            //一旦变为false就不需要访问redis了
            longOverMap.put(commodityId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //第二步判断是否秒杀到了
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdCommodityId(user.getId(), commodityId);
        if (seckillOrder != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //第三步进行排队
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setCommodityId(commodityId);
        mqSender.sendMessage(message);
        return Result.success(0);
        /*
        // System.out.println(commodityId);
        // System.out.println(user.getNickname());
        // 判断库存 10个商品，一个用户同事发出两个请求req1 req2，那么就可以同时减库存了
        // 解决：加一个唯一索引
        // 这里在大并发的时候会出现负库存的情况
        CommodityVo commodityVo = commodityService.getCommodityVoById(commodityId);

        Integer stockCount = commodityVo.getStockCount();
        if (stockCount<= 0) {
            //model.addAttribute("erroMsg", CodeMsg.SECKILLING_OVER.getMsg());
            return Result.error(CodeMsg.SECKILLING_OVER);
        }
        //判断是否秒杀到了，是为了防止一个人秒杀到了多个商品，进入order中来查询
        SeckillingOrder seckillingOrder = orderService.getSeckillingOrderByUserIdCommodityId(user.getId(), commodityId);
        if (seckillingOrder != null) {
            //model.addAttribute("errorMsg", CodeMsg.REPEATE_SECKILLING.getMsg());
            return Result.error(CodeMsg.REPEATE_SECKILLING);
        }
        //下订单并写入秒杀订单
        OrderInfo orderInfo = seckillingService.seckilling(user, commodityVo);
        System.out.println(orderInfo.toString());

        //model.addAttribute("orderInfo", orderInfo);
        //model.addAttribute("commodity", commodityVo);
        return Result.success(orderInfo);
        //return "hello";
        */
    }

    @RequestMapping(value = "/seckill_result", method = RequestMethod.GET)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("commodityId")Long commodityId) {
    public Result<Long> secSkillResult(User user, @RequestParam("commodityId")Long commodityId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //获取状态
        /*
            orderId: 秒杀成功
            -1: 秒杀失败
            0: 排队成功
         */
        Long result = seckillService.getSeckillResult(user.getId(), commodityId);
        return Result.success(result);
    }

    @RequestMapping(value = "/getSeckillPath", method = RequestMethod.GET)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("commodityId")Long commodityId) {
    public Result<String> getSeckillPath(User user, @RequestParam("commodityId")Long commodityId, @RequestParam("verifyCode")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        boolean checkVerifyCode = seckillService.checkVerifyCode(user, commodityId, verifyCode);
        if (!checkVerifyCode) {
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String seckillPath = seckillService.createSeckillPath(user, commodityId);
        return Result.success(seckillPath);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("commodityId")Long commodityId) {
    public Result<String> getVerifyCode(User user, HttpServletResponse response, @RequestParam("commodityId")Long commodityId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        BufferedImage verifyCodeImg = seckillService.createVerifyCodeImg(user, commodityId);
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
