package top.candyboy.redis;

public interface KeyPrefix {
    public int expireSeconds();
    public String getPrefix();
}
