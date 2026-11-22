package io.jiangbyte.github.api.admin.content.announcement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发布或重新发布公告请求。
 */
@Data
public class PublishAnnouncementRequestDTO {
    @NotNull(message = "不能为空")
    @Positive(message = "必须大于 0")
    private Long id;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
}
