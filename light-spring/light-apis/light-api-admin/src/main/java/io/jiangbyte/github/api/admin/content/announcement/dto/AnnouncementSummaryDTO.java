package io.jiangbyte.github.api.admin.content.announcement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Admin 公告分页摘要，不包含完整正文。
 */
@Data
public class AnnouncementSummaryDTO {
    private Long id;
    private String title;
    private String summary;
    private String type;
    private String priority;
    private String status;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
    private LocalDateTime updatedAt;
}
