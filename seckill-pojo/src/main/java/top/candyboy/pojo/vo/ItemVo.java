package top.candyboy.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ItemVo {
    private Long id;
    private String itemName;
    private String itemTitle;
    private String itemImg;
    private String itemDetail;
    private Double itemPrice;
    private Integer itemStock;
    private Double seckillingPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
