package top.candyboy.pojo.vo;

import lombok.Data;
import top.candyboy.pojo.Commodity;
import top.candyboy.pojo.User;

@Data
public class CommodityDetailVo {
    private int seckillingStatus;
    private int remainSeconds;
    private Commodity commodity;
    private CommodityVo commodityVo;
    private User user;
}
