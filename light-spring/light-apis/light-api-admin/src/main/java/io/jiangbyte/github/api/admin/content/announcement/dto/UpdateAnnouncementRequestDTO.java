package io.jiangbyte.github.api.admin.content.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新公告请求；已发布公告更新时发布时间不能为空。
 */
@Data
public class UpdateAnnouncementRequestDTO {
    @NotNull(message = "不能为空")
    @Positive(message = "必须大于 0")
    private Long id;
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
