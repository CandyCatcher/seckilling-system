package top.candyboy.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillItem {
    private Long id;
    private Long itemId;
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
