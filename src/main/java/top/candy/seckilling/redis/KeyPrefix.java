package top.candy.seckilling.redis;

public interface KeyPrefix {
    public int expireSeconds();
    public String getPrefix();
}
