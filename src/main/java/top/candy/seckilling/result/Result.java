package top.candy.seckilling.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    //data可以是任意类型
    private T data;

    //设置为private是希望用户只能通过方法调用构造函数
    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    //失败时调用的构造函数
    private Result(CodeMsg codeMsg) {
        if (codeMsg == null) {
            return;
        } else {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    /*
      成功时返回
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /*
    失败时调用,希望传进来一个对象，里面保存了code和msg
     */
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
}
