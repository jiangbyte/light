package io.jiangbyte.github.trigger.admin.content.announcement.mapper;

import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.AnnouncementSummaryDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.CreateAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.PublishAnnouncementRequestDTO;
import io.jiangbyte.github.api.admin.content.announcement.dto.UpdateAnnouncementRequestDTO;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.CreateAnnouncementCommand;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.PublishAnnouncementCommand;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.UpdateAnnouncementCommand;
import io.jiangbyte.github.types.page.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Admin 公告 API DTO 与 Case/Domain 模型映射。
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdminAnnouncementMapper {
    @Mapping(target = "type", qualifiedByName = "parseType")
    @Mapping(target = "priority", qualifiedByName = "parsePriority")
    CreateAnnouncementCommand toCommand(CreateAnnouncementRequestDTO request);

    @Mapping(target = "type", qualifiedByName = "parseType")
    @Mapping(target = "priority", qualifiedByName = "parsePriority")
    UpdateAnnouncementCommand toCommand(UpdateAnnouncementRequestDTO request);

    PublishAnnouncementCommand toCommand(PublishAnnouncementRequestDTO request);

    AnnouncementDTO toDto(AnnouncementEntity announcement);

    AnnouncementSummaryDTO toSummaryDto(AnnouncementEntity announcement);

    PageResult<AnnouncementSummaryDTO> toPageDto(PageResult<AnnouncementEntity> page);

    @Named("parseType")
    default AnnouncementType parseType(String value) {
        return AnnouncementType.parse(value);
    }

    @Named("parsePriority")
    default AnnouncementPriority parsePriority(String value) {
        return AnnouncementPriority.parse(value);
    }
}
