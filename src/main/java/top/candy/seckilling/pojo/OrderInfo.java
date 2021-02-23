package top.candy.seckilling.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfo {
    private Long id;
    private Long userId;
    private Long commodityId;
    private Long  deliveryAddrId;
    private String commodityName;
    private Integer commodityCount;
    private Double commodityPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;
}
