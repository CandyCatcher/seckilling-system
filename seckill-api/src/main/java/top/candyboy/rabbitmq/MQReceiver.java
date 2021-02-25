package top.candyboy.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candyboy.pojo.SeckillOrder;
import top.candyboy.pojo.User;
import top.candyboy.service.RedisService;
import top.candyboy.service.CommodityService;
import top.candyboy.service.OrderService;
import top.candyboy.service.SeckillService;
import top.candyboy.pojo.vo.CommodityVo;

@Service
public class MQReceiver {
    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    RedisService redisService;
    OrderService orderService;
    CommodityService commodityService;
    SeckillService seckillService;

    @Autowired
    public static void setLogger(Logger logger) {
        MQReceiver.logger = logger;
    }
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

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        logger.info("receiver message" + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);

        User user = seckillMessage.getUser();
        Long commodityId = seckillMessage.getCommodityId();
        // 判断库存
        CommodityVo commodityVo = commodityService.getCommodityVoById(commodityId);
        Integer stockCount = commodityVo.getStockCount();
        if (stockCount<= 0) {
            return;
        }
        //判断是否已秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdCommodityId(user.getId(), commodityId);
        if (seckillOrder != null) {
            System.out.println("已经");
            return;
        }
        //下订单并写入秒杀订单
        seckillService.doSeckill(user, commodityVo);
    }

   /* @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        logger.info("topic queue1 message" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        logger.info("topic queue2 message" + message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveHeader(byte[] message) {
        logger.info("header queue message:  " + new String(message));
    }*/

}