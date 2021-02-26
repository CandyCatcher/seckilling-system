package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candyboy.OrderDao;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.SeckillOrder;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.OrderKey;
import top.candyboy.pojo.vo.ItemVo;

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

    public SeckillOrder getSeckillOrderByUserIdItemId(Long userId, Long itemId) {
        //return orderDao.getSeckillOrderByUserIdItemId(userId, itemId);
    //    去查缓存
        SeckillOrder seckillOrder =  redisService.get(OrderKey.getSeckillOrderByUidGid, userId + "_" + itemId, SeckillOrder.class);
        return seckillOrder;
    }

    public OrderInfo createOrder(User user, ItemVo itemVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setItemCount(1);
        orderInfo.setItemId(itemVo.getId());
        orderInfo.setItemName(itemVo.getItemName());
        orderInfo.setItemPrice(itemVo.getSeckillingPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrder(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setItemId(itemVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        //System.out.println(seckillOrder.getItemId());
        orderDao.insertSeckillOrder(seckillOrder);
        redisService.set(OrderKey.getSeckillOrderByUidGid, user.getId() + "_" + itemVo.getId(), seckillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderInfoById(Long orderId) {
        return orderDao.getOrderInfoById(orderId);
    }
}
