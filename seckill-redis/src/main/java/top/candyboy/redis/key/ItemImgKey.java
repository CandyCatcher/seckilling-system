package top.candyboy.redis.key;

import top.candyboy.redis.BasePrefix;

public class ItemImgKey extends BasePrefix {
    public ItemImgKey(String prefix) {
        super(prefix);
    }

    public ItemImgKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //不让时间缓存时间过长
    public static ItemImgKey getItemImg = new ItemImgKey(60, "IIK");
}
