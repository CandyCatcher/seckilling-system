package top.candyboy.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class ItemImg {

    @Id
    private String id;
    @Column(name = "item_id")
    private String itemId;

    private String url;

    private Integer sort;

    @Column(name = "is_main")
    private Integer isMain;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

}
