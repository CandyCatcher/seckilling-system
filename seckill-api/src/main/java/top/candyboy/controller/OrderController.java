package top.candyboy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.User;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.ItemService;
import top.candyboy.service.OrderService;
import top.candyboy.service.SeckillService;
import top.candyboy.pojo.vo.ItemVo;
import top.candyboy.pojo.vo.OrderDetailVo;

@RestController
public class OrderController {
    OrderService orderService;
    ItemService itemService;
    SeckillService seckillService;
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }
    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    @Autowired
    public void setSeckillService(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @GetMapping(value = "/pay")
    public Result<OrderDetailVo> orderDetail(User user, @RequestParam("orderId")Long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXSIT);
        }
        Long itemId = orderInfo.getItemId();
        ItemVo itemVo = itemService.getItemVoById(itemId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setItemVo(itemVo);
        orderDetailVo.setOrderInfo(orderInfo);

        return Result.success(orderDetailVo);
    }
}
