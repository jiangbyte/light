package io.jiangbyte.github.api.admin.content.announcement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Admin 公告详情。
 */
@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String type;
    private String priority;
    private String status;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
