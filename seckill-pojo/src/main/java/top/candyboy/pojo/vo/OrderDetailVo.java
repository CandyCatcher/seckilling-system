package top.candyboy.pojo.vo;

import lombok.Data;
import top.candyboy.pojo.OrderInfo;

@Data
public class OrderDetailVo {
    private ItemVo itemVo;
    private OrderInfo orderInfo;
}
