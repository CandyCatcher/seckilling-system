package top.candyboy.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfo {
    private Long id;
    private Long userId;
    private Long itemId;
    private Long  deliveryAddrId;
    private String itemName;
    private Integer itemCount;
    private Double itemPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;
}
