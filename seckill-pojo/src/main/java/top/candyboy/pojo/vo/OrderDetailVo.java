package top.candyboy.pojo.vo;

import lombok.Data;
import top.candyboy.pojo.OrderInfo;

@Data
public class OrderDetailVo {
    private CommodityVo commodityVo;
    private OrderInfo orderInfo;
}
