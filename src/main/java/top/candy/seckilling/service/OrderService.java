package top.candy.seckilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.candy.seckilling.dao.OrderDao;
import top.candy.seckilling.pojo.OrderInfo;
import top.candy.seckilling.pojo.SeckillOrder;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.redis.OrderKey;
import top.candy.seckilling.redis.RedisService;
import top.candy.seckilling.vo.CommodityVo;

import java.util.Date;

@Service
public class OrderService {

    OrderDao orderDao;
    RedisService redisService;
    @Autowired
    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public SeckillOrder getSeckillOrderByUserIdCommodityId(Long userId, Long commodityId) {
        //return orderDao.getSeckillOrderByUserIdCommodityId(userId, commodityId);
    //    去查缓存
        SeckillOrder seckillOrder =  redisService.get(OrderKey.getSeckillOrderByUidGid, userId + "_" + commodityId, SeckillOrder.class);
        return seckillOrder;
    }

    public OrderInfo createOrder(User user, CommodityVo commodityVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setCommodityCount(1);
        orderInfo.setCommodityId(commodityVo.getId());
        orderInfo.setCommodityName(commodityVo.getCommodityName());
        orderInfo.setCommodityPrice(commodityVo.getSeckillingPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrder(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setCommodityId(commodityVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        //System.out.println(seckillOrder.getCommodityId());
        orderDao.insertSeckillOrder(seckillOrder);
        redisService.set(OrderKey.getSeckillOrderByUidGid, user.getId() + "_" + commodityVo.getId(), seckillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderInfoById(Long orderId) {
        return orderDao.getOrderInfoById(orderId);
    }
}
