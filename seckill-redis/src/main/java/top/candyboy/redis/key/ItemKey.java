package top.candyboy.redis.key;

import top.candyboy.redis.BasePrefix;

public class ItemKey extends BasePrefix {
    public ItemKey(String prefix) {
        super(prefix);
    }

    public ItemKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //不让时间缓存时间过长
    public static ItemKey getItemList = new ItemKey(60, "ck");
    public static ItemKey getItemDetail = new ItemKey(60, "cd");
    public static ItemKey getItemId = new ItemKey("itemId");
    public static ItemKey getItemStock = new ItemKey("itemStock");
    public static ItemKey stockOver = new ItemKey("stockOver");

}
