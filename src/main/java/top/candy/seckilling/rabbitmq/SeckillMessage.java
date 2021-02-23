package top.candy.seckilling.rabbitmq;

import lombok.Data;
import top.candy.seckilling.pojo.User;

@Data
public class SeckillMessage {
    private User user;
    private Long commodityId;
}
