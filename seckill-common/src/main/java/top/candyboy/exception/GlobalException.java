package top.candyboy.exception;

import lombok.Data;
import top.candyboy.result.CodeMsg;

@Data
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }
}
