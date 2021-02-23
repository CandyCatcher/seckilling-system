package top.candy.seckilling.redis;

public class UserKey extends BasePrefix {
    public UserKey(String prefix) {
        super(prefix);
    }

    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;
    //设置过期时间
    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"tk");
}
