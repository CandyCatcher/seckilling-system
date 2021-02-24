package top.candyboy.result;

import lombok.Data;

@Data
public class CodeMsg {
    public static final CodeMsg SECKILL_FAIL = new CodeMsg(500502, "秒杀失败");
    public static final CodeMsg VERIFYCODE_ERROR = new CodeMsg(500503, "验证码错误");
    private int code;
    private String msg;

    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
    //这里带有参数
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常:%s");
    //这里可以设置多个不同类型的错误
    //登录模块5002XX
    public static final CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "密码不能为空");
    public static final CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号码不能为空");
    public static final CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号码格式错误");
    public static final CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号码不存在");
    public static final CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");

    public static final CodeMsg SECKILL_OVER = new CodeMsg(500500, "商品已无库存");
    public static final CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "已经下过订单了");

    public static final CodeMsg ORDER_NOT_EXSIT = new CodeMsg(500400, "订单不存在");
    public static final CodeMsg PATH_ILLEGEAL = new CodeMsg(500401, "请求非法");

    public static final CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500402, "请求次数过多");

    private CodeMsg(int i, String msg) {
        this.code = i;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
