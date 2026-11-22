package io.jiangbyte.github.domain.content.announcement.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;

import java.util.Locale;

/**
 * 公告优先级。
 */
public enum AnnouncementPriority {
    NORMAL,
    IMPORTANT,
    URGENT;

    /**
     * 解析公告优先级；非法值转换为统一参数错误。
     */
    public static AnnouncementPriority parse(String value) {
        if (StrUtil.isBlank(value)) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER, "公告优先级不能为空");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "公告优先级只能是 NORMAL、IMPORTANT 或 URGENT");
        }
    }

    /**
     * 解析可空公告优先级，供查询条件使用。
     */
    public static AnnouncementPriority parseNullable(String value) {
        return StrUtil.isBlank(value) ? null : parse(value);
    }
}
