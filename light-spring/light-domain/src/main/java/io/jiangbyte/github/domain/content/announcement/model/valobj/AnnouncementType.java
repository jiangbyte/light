package io.jiangbyte.github.domain.content.announcement.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;

import java.util.Locale;

/**
 * 公告内容类型。
 */
public enum AnnouncementType {
    NOTICE,
    ANNOUNCEMENT,
    MAINTENANCE;

    /**
     * 解析公告类型；空值表示未指定，非法值转换为统一参数错误。
     */
    public static AnnouncementType parse(String value) {
        if (StrUtil.isBlank(value)) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER, "公告类型不能为空");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "公告类型只能是 NOTICE、ANNOUNCEMENT 或 MAINTENANCE");
        }
    }

    /**
     * 解析可空公告类型，供查询条件使用。
     */
    public static AnnouncementType parseNullable(String value) {
        return StrUtil.isBlank(value) ? null : parse(value);
    }
}
