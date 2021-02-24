package top.candyboy.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommodityVo {
    private Long id;
    private String commodityName;
    private String commodityTitle;
    private String commodityImg;
    private String commodityDetail;
    private Double commodityPrice;
    private Integer commodityStock;
    private Double seckillingPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
