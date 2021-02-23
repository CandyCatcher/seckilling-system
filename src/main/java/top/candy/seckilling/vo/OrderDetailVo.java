package top.candy.seckilling.vo;

import lombok.Data;
import top.candy.seckilling.pojo.OrderInfo;

@Data
public class OrderDetailVo {
    private CommodityVo commodityVo;
    private OrderInfo orderInfo;
}
