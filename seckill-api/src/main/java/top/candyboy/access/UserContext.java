package top.candyboy.access;

import top.candyboy.pojo.User;

public class UserContext {

    //跟当前线程绑定的
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }

}
