package io.jiangbyte.github.types.exception;

import io.jiangbyte.github.types.enums.ResponseCode;
import lombok.Getter;

/**
 * 业务异常；与 Web 框架无关，由各端全局异常处理转为统一 Response JSON。
 */
@Getter
public class BizException extends RuntimeException {

    private final String code;
    /** HTTP 状态码数值，默认 400；由 Web 层映射，不依赖 Spring。 */
    private final int httpStatus;

    public BizException(String code, String message) {
        this(code, message, 400);
    }

    public BizException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getInfo(), 400);
    }

    public BizException(ResponseCode responseCode, String message) {
        this(responseCode.getCode(), message, 400);
    }

    public BizException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus > 0 ? httpStatus : 400;
    }

}
