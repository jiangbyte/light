package io.jiangbyte.github.domain.content.announcement.model.entity;

import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 通知公告实体，同时作为公告一致性边界。
 */
@Getter
@AllArgsConstructor
public final class AnnouncementEntity {
    private final Long id;
    private final String title;
    private final String summary;
    private final String content;
    private final AnnouncementType type;
    private final AnnouncementPriority priority;
    private final AnnouncementStatus status;
    private final LocalDateTime publishAt;
    private final LocalDateTime expireAt;
    private final Long createdBy;
    private final Long updatedBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
