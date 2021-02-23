package top.candy.seckilling.util;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid() {
        //去掉横杠
        return UUID.randomUUID().toString().replace("-", "");
    }
}
