package io.jiangbyte.github.api.admin.content.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建公告请求。
 */
@Data
public class CreateAnnouncementRequestDTO {
    @NotBlank(message = "不能为空")
    @Size(max = 120, message = "长度不能超过 120")
    private String title;
    @Size(max = 500, message = "长度不能超过 500")
    private String summary;
    @NotBlank(message = "不能为空")
    @Size(max = 100000, message = "长度不能超过 100000")
    private String content;
    @NotBlank(message = "不能为空")
    private String type;
    @NotBlank(message = "不能为空")
    private String priority;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
}
