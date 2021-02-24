package top.candyboy.redis.key;

import top.candyboy.redis.BasePrefix;

public class CommodityKey extends BasePrefix {
    public CommodityKey(String prefix) {
        super(prefix);
    }

    public CommodityKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //不让时间缓存时间过长
    public static CommodityKey getCommodityList = new CommodityKey(60, "ck");
    public static CommodityKey getCommodityDetail = new CommodityKey(60, "cd");
    public static CommodityKey getCommodityId = new CommodityKey("commodityId");
    public static CommodityKey stockOver = new CommodityKey("stockOver");

}
