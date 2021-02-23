package top.candy.seckilling.vo;

import lombok.Data;
import top.candy.seckilling.pojo.Commodity;

import java.util.Date;

@Data
public class CommodityVo extends Commodity {
    private Double seckillingPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
