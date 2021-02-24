package top.candyboy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.User;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.CommodityService;
import top.candyboy.service.OrderService;
import top.candyboy.service.SeckillService;
import top.candyboy.pojo.vo.CommodityVo;
import top.candyboy.pojo.vo.OrderDetailVo;

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
