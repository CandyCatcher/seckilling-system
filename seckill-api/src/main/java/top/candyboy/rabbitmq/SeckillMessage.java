package top.candyboy.rabbitmq;

import lombok.Data;
import top.candyboy.pojo.User;

/*
里面存的信息就是哪个用户秒杀的哪个商品
 */
@Data
public class SeckillMessage {
    private User user;
    private Long itemId;
}
