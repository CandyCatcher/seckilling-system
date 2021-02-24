package top.candyboy.pojo;

import lombok.Data;

@Data
public class Commodity {
    private Long id;
    private String commodityName;
    private String commodityTitle;
    private String commodityImg;
    private String commodityDetail;
    private Double commodityPrice;
    private Integer commodityStock;
}