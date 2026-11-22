package io.jiangbyte.github.api.admin.content.announcement;

import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementPageRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementSummaryDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.CreateAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.PublishAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.UpdateAnnouncementRequestDTO;
import io.jiangbyte.github.types.page.PageResult;
import io.jiangbyte.github.types.request.IdRequestDTO;
import io.jiangbyte.github.types.request.IdsRequestDTO;
import io.jiangbyte.github.types.response.Response;

/**
 * 管理端通知公告 CRUD 与生命周期接口。
 */
public interface IAdminAnnouncementService {
    Response<PageResult<AnnouncementSummaryDTO>> page(AnnouncementPageRequestDTO request);

    Response<AnnouncementDTO> detail(Long id);

    Response<AnnouncementDTO> create(CreateAnnouncementRequestDTO request);

    Response<AnnouncementDTO> update(UpdateAnnouncementRequestDTO request);

    Response<Void> delete(IdsRequestDTO request);

    Response<AnnouncementDTO> publish(PublishAnnouncementRequestDTO request);

    Response<AnnouncementDTO> archive(IdRequestDTO request);
}
