package io.jiangbyte.github.trigger.admin.content.announcement.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 发布或重新发布公告用例输入。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishAnnouncementCommand {
    private long id;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
}
