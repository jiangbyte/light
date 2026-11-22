package io.jiangbyte.github.trigger.admin.content.announcement.http;

import io.jiangbyte.github.api.admin.content.announcement.IAdminAnnouncementService;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementPageRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementSummaryDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.CreateAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.PublishAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.UpdateAnnouncementRequestDTO;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.trigger.admin.content.announcement.application.AdminAnnouncementApplicationService;
import io.jiangbyte.github.trigger.admin.content.announcement.mapper.AdminAnnouncementMapper;
import io.jiangbyte.github.types.page.PageResult;
import io.jiangbyte.github.types.response.Response;
import io.jiangbyte.github.types.request.IdRequestDTO;
import io.jiangbyte.github.types.request.IdsRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 通知公告管理入口。
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController implements IAdminAnnouncementService {
    private final AdminAnnouncementApplicationService announcementApplicationService;
    private final AdminAnnouncementMapper announcementMapper;

    @Override
    @GetMapping("/page")
    public Response<PageResult<AnnouncementSummaryDTO>> page(
            @Valid @ModelAttribute AnnouncementPageRequestDTO request) {
        PageResult<AnnouncementEntity> page = announcementApplicationService.page(
                AnnouncementPageQuery.of(request.getPage(), request.getPageSize(), request.getKeyword(),
                        request.getType(), request.getPriority(), request.getStatus()));
        return Response.success(announcementMapper.toPageDto(page));
    }

    @Override
    @GetMapping("/detail")
    public Response<AnnouncementDTO> detail(@RequestParam Long id) {
        return Response.success(announcementMapper.toDto(announcementApplicationService.detail(id)));
    }

    @Override
    @PostMapping("/create")
    public Response<AnnouncementDTO> create(@Valid @RequestBody CreateAnnouncementRequestDTO request) {
        return Response.success(announcementMapper.toDto(
                announcementApplicationService.create(announcementMapper.toCommand(request))));
    }

    @Override
    @PostMapping("/update")
    public Response<AnnouncementDTO> update(@Valid @RequestBody UpdateAnnouncementRequestDTO request) {
        return Response.success(announcementMapper.toDto(
                announcementApplicationService.update(announcementMapper.toCommand(request))));
    }

    @Override
    @PostMapping("/delete")
    public Response<Void> delete(@Valid @RequestBody IdsRequestDTO request) {
        announcementApplicationService.delete(request.getIds());
        return Response.success();
    }

    @Override
    @PostMapping("/publish")
    public Response<AnnouncementDTO> publish(@Valid @RequestBody PublishAnnouncementRequestDTO request) {
        return Response.success(announcementMapper.toDto(
                announcementApplicationService.publish(announcementMapper.toCommand(request))));
    }

    @Override
    @PostMapping("/archive")
    public Response<AnnouncementDTO> archive(@Valid @RequestBody IdRequestDTO request) {
        return Response.success(announcementMapper.toDto(
                announcementApplicationService.archive(request.getId())));
    }
}
