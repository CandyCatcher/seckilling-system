package top.candyboy.redis.key;

import top.candyboy.redis.BasePrefix;

public class OrderKey extends BasePrefix {
    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("SOUG");
}
