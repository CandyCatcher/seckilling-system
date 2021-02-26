package top.candyboy.pojo;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String itemName;
    private String itemTitle;
    private String itemDetail;
    private Double itemPrice;
    private Integer itemStock;
}