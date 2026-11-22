package io.jiangbyte.github.api.portal.content.announcement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Portal 公告分页摘要，不包含完整正文。
 */
@Data
public class PortalAnnouncementSummaryDTO {
    private Long id;
    private String title;
    private String summary;
    private String type;
    private String priority;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
    private LocalDateTime updatedAt;
}
