package io.jiangbyte.github.trigger.portal.content.announcement.mapper;

import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementDTO;
import io.jiangbyte.github.api.portal.content.announcement.dto.PortalAnnouncementSummaryDTO;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.types.page.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Portal 公告领域对象到 API DTO 的映射。
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PortalAnnouncementMapper {
    PortalAnnouncementDTO toDto(AnnouncementEntity announcement);

    PortalAnnouncementSummaryDTO toSummaryDto(AnnouncementEntity announcement);

    PageResult<PortalAnnouncementSummaryDTO> toPageDto(PageResult<AnnouncementEntity> page);
}
