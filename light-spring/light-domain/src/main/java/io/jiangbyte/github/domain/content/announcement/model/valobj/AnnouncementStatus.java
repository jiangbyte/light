package io.jiangbyte.github.domain.content.announcement.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;

import java.util.Locale;

/**
 * 公告生命周期状态。
 */
public enum AnnouncementStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED;

    /**
     * 解析公告状态；非法值转换为统一参数错误。
     */
    public static AnnouncementStatus parse(String value) {
        if (StrUtil.isBlank(value)) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER, "公告状态不能为空");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "公告状态只能是 DRAFT、PUBLISHED 或 ARCHIVED");
        }
    }

    /**
     * 解析可空公告状态，供查询条件使用。
     */
    public static AnnouncementStatus parseNullable(String value) {
        return StrUtil.isBlank(value) ? null : parse(value);
    }
}
