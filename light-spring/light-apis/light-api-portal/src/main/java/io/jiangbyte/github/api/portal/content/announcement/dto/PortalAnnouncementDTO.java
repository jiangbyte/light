package io.jiangbyte.github.api.portal.content.announcement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Portal 已发布公告详情。
 */
@Data
public class PortalAnnouncementDTO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String type;
    private String priority;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
