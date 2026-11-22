package io.jiangbyte.github.domain.account.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;

import java.util.Locale;

/**
 * 账户状态。禁用账户不能登录，已签发会话也会被统一踢出。
 */
public enum AccountStatus {
    ACTIVE,
    DISABLED;

    /**
     * 解析可空状态文本，并统一返回业务参数错误。
     */
    public static AccountStatus parseNullable(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return valueOf(StrUtil.trim(value).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER, "账户状态只能是 ACTIVE 或 DISABLED");
        }
    }
}
