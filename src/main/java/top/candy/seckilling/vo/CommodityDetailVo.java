package top.candy.seckilling.vo;

import lombok.Data;
import top.candy.seckilling.pojo.Commodity;
import top.candy.seckilling.pojo.User;

@Data
public class CommodityDetailVo {
    private int seckillingStatus;
    private int remainSeconds;
    private Commodity commodity;
    private User user;
}
