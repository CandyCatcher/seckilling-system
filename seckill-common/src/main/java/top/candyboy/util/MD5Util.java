package top.candyboy.util;

import org.springframework.util.DigestUtils;

/**
 * 为什么要做两次md5？
 * 对于http传输
 * 用户输入明文密码，如果不加密就可以被截取
 * 在传输之前做一次md5，放到服务端（如果数据库被盗了，比如说彩虹表反查）
 * 服务端拿到这个md5，并不是直接存储，而是生成一个salt，和用户输入的密码做一个拼装
 * 把md5和sal一起存储
 */
public class MD5Util {

    private static final String salt = "1A0tN54d";

    public static String md5(String string) {
        return DigestUtils.md5DigestAsHex(string.getBytes());
    }

    /*
    做第一次md5
     */
    public static String inputPassToFormPass(String inputPass) {
        //做一次拼装，然后再做md5
        //如果不加“”的话，前面是进行数字计算
        String str = "" + salt.charAt(0) + salt.charAt(7) + inputPass + salt.charAt(4) + salt.charAt(3);
        System.out.println("str:" + str);

        return md5(str);
    }

    /*
    做第二次md5
    第一个salt不写死，服务端就没法破解密码了
    第二个salt是可以存入到服务器中的，所以随机的
     */
    // 结果使用的是类中的salt
    public static String formPassToDBPass(String formPass, String saltDB) {
        String str = saltDB.charAt(1) + saltDB.charAt(3) + formPass + saltDB.charAt(5) + saltDB.charAt(7);
        return md5(str);
    }

    /*
    用户输入的明文密码直接转换成数据库的密码
     */
    public static String inputPassToDBPass(String inputPass, String saltDB) {
        String str = inputPassToFormPass(inputPass);
        return formPassToDBPass(str, saltDB);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("tian1234"));
        // 1c1e6545ed4ca306097488fbd0fae6f7
        System.out.println(formPassToDBPass("a3e0bc5901e43f431d057cbb31ed5b6f", "D2n4L0r9"));
        // 76a1290cda0be7d3c960d9bbc72ab41b

        //System.out.println(inputPassToDBPass("tian1234", "D2n4L0r9"));
        // 588cfa22f8b26546eb0e528662175dd1

        // LoginVo(a3e0bc5901e43f431d057cbb31ed5b6f
    }
}
