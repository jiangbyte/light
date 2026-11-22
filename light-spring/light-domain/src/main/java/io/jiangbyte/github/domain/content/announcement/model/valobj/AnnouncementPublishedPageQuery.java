package io.jiangbyte.github.domain.content.announcement.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import io.jiangbyte.github.types.page.PageConstants;

/**
 * Portal 已发布公告分页查询条件。
 */
public record AnnouncementPublishedPageQuery(int page, int pageSize, String keyword,
                                             AnnouncementType type, AnnouncementPriority priority) {
    public AnnouncementPublishedPageQuery {
        if (page < PageConstants.DEFAULT_PAGE || pageSize < 1
                || pageSize > PageConstants.MAX_PAGE_SIZE) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "page 必须大于 0，pageSize 必须在 1 到 "
                            + PageConstants.MAX_PAGE_SIZE + " 之间");
        }
        keyword = StrUtil.trimToNull(keyword);
    }

    /**
     * 从可空分页参数创建 Portal 查询对象。
     */
    public static AnnouncementPublishedPageQuery of(Integer page, Integer pageSize, String keyword,
                                                     String type, String priority) {
        return new AnnouncementPublishedPageQuery(
                page == null ? PageConstants.DEFAULT_PAGE : page,
                pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize,
                keyword,
                AnnouncementType.parseNullable(type),
                AnnouncementPriority.parseNullable(priority)
        );
    }
}
