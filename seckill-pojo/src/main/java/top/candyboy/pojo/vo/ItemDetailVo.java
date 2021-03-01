package top.candyboy.pojo.vo;

import lombok.Data;
import top.candyboy.pojo.Item;
import top.candyboy.pojo.ItemImg;
import top.candyboy.pojo.User;

import java.util.List;

@Data
public class ItemDetailVo {
    private int seckillingStatus;
    private int remainSeconds;
    private Item item;
    private ItemVo itemVo;
    private List<ItemImg> itemImg;
    private User user;
}
