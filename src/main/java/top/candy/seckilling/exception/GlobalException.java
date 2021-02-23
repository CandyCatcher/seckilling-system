package top.candy.seckilling.exception;

import lombok.Data;
import top.candy.seckilling.result.CodeMsg;

@Data
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }
}
