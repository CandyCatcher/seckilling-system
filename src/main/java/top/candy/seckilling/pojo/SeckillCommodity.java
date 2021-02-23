package top.candy.seckilling.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillCommodity {
    private Long id;
    private Long commodityId;
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
