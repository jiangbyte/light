package io.jiangbyte.github.types.response;

import io.jiangbyte.github.types.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用接口响应包装；Admin / Portal 共用。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7000723935764546321L;

    private String code;
    private String message;
    private T data;

    /**
     * 构造成功响应（无业务数据）。
     */
    public static <T> Response<T> success() {
        return success(null);
    }

    /**
     * 构造成功响应。
     */
    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getInfo())
                .data(data)
                .build();
    }

    /**
     * 构造失败响应。
     */
    public static <T> Response<T> fail(String code, String message) {
        return Response.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * 按响应码枚举构造失败响应。
     */
    public static <T> Response<T> fail(ResponseCode responseCode) {
        return fail(responseCode.getCode(), responseCode.getInfo());
    }

    /**
     * 按响应码枚举构造失败响应，并覆盖提示文案。
     */
    public static <T> Response<T> fail(ResponseCode responseCode, String message) {
        return fail(responseCode.getCode(), message);
    }

}
