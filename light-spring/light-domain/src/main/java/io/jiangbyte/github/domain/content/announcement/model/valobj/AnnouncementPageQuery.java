package io.jiangbyte.github.domain.content.announcement.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import io.jiangbyte.github.types.page.PageConstants;

/**
 * Admin 公告分页查询条件。
 */
public record AnnouncementPageQuery(int page, int pageSize, String keyword,
                                    AnnouncementType type, AnnouncementPriority priority,
                                    AnnouncementStatus status) {
    public AnnouncementPageQuery {
        if (page < PageConstants.DEFAULT_PAGE || pageSize < 1
                || pageSize > PageConstants.MAX_PAGE_SIZE) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "page 必须大于 0，pageSize 必须在 1 到 "
                            + PageConstants.MAX_PAGE_SIZE + " 之间");
        }
        keyword = StrUtil.trimToNull(keyword);
    }

    /**
     * 从可空分页参数创建 Admin 查询对象。
     */
    public static AnnouncementPageQuery of(Integer page, Integer pageSize, String keyword,
                                           String type, String priority, String status) {
        return new AnnouncementPageQuery(
                page == null ? PageConstants.DEFAULT_PAGE : page,
                pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize,
                keyword,
                AnnouncementType.parseNullable(type),
                AnnouncementPriority.parseNullable(priority),
                AnnouncementStatus.parseNullable(status)
        );
    }
}
