package top.candyboy.redis.key;

import top.candyboy.redis.BasePrefix;
import top.candyboy.redis.KeyPrefix;

public class SeckillKey extends BasePrefix {
    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey getPath = new SeckillKey(60, "path");
    public static KeyPrefix getSeckillVerifyCode = new SeckillKey(300, "verifyCode");

}
