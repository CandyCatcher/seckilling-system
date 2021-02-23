package top.candy.seckilling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.candy.seckilling.pojo.OrderInfo;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.result.CodeMsg;
import top.candy.seckilling.result.Result;
import top.candy.seckilling.service.CommodityService;
import top.candy.seckilling.service.OrderService;
import top.candy.seckilling.service.SeckillService;
import top.candy.seckilling.vo.CommodityVo;
import top.candy.seckilling.vo.OrderDetailVo;

@Controller
public class OrderController {
    OrderService orderService;
    CommodityService commodityService;
    SeckillService seckillService;
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

    @RequestMapping("orderDetail")
    @ResponseBody
    public Result<OrderDetailVo> orderDetail(User user, @RequestParam("orderId")Long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXSIT);
        }
        Long commodityId = orderInfo.getCommodityId();
        CommodityVo commodityVo = commodityService.getCommodityVoById(commodityId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setCommodityVo(commodityVo);
        orderDetailVo.setOrderInfo(orderInfo);

        return Result.success(orderDetailVo);
    }
}
