package io.jiangbyte.github.api.portal.content.announcement;

import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementDTO;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementPageRequestDTO;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementSummaryDTO;
import io.jiangbyte.github.types.page.PageResult;
import io.jiangbyte.github.types.response.Response;

/**
 * Portal 已发布通知公告查询接口。
 */
public interface IPortalAnnouncementService {
    Response<PageResult<PortalAnnouncementSummaryDTO>> page(PortalAnnouncementPageRequestDTO request);

    Response<PortalAnnouncementDTO> detail(Long id);
}
