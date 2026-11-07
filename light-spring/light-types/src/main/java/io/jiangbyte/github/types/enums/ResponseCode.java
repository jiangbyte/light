package io.jiangbyte.github.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 统一业务响应码。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS("0", "成功"),
    UN_ERROR("1", "未知失败"),
    ILLEGAL_PARAMETER("400", "非法参数"),
    NOT_FOUND("404", "资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法不支持"),
    ;

    private String code;
    private String info;

}
