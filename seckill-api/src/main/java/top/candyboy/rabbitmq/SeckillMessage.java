package top.candyboy.rabbitmq;

import lombok.Data;
import top.candyboy.pojo.User;

@Data
public class SeckillMessage {
    private User user;
    private Long itemId;
}
