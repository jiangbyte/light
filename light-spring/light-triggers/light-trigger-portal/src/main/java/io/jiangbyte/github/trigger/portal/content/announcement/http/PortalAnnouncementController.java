package io.jiangbyte.github.trigger.portal.content.announcement.http;

import io.jiangbyte.github.api.portal.content.announcement.IPortalAnnouncementService;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementDTO;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementPageRequestDTO;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementSummaryDTO;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPublishedPageQuery;
import io.jiangbyte.github.trigger.portal.content.announcement.application.PortalAnnouncementApplicationService;
import io.jiangbyte.github.trigger.portal.content.announcement.mapper.PortalAnnouncementMapper;
import io.jiangbyte.github.types.page.PageResult;
import io.jiangbyte.github.types.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portal 已发布通知公告查询入口。
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class PortalAnnouncementController implements IPortalAnnouncementService {
    private final PortalAnnouncementApplicationService announcementApplicationService;
    private final PortalAnnouncementMapper announcementMapper;

    @Override
    @GetMapping("/page")
    public Response<PageResult<PortalAnnouncementSummaryDTO>> page(
            @Valid @ModelAttribute PortalAnnouncementPageRequestDTO request) {
        PageResult<AnnouncementEntity> page = announcementApplicationService.publishedPage(
                AnnouncementPublishedPageQuery.of(request.getPage(), request.getPageSize(),
                        request.getKeyword(), request.getType(), request.getPriority()));
        return Response.success(announcementMapper.toPageDto(page));
    }

    @Override
    @GetMapping("/detail")
    public Response<PortalAnnouncementDTO> detail(@RequestParam Long id) {
        return Response.success(announcementMapper.toDto(
                announcementApplicationService.publishedDetail(id)));
    }
}
