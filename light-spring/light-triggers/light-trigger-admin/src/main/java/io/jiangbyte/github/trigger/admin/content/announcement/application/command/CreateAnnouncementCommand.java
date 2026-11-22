package io.jiangbyte.github.trigger.admin.content.announcement.application.command;

import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 创建公告用例输入。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnouncementCommand {
    private String title;
    private String summary;
    private String content;
    private AnnouncementType type;
    private AnnouncementPriority priority;
    private LocalDateTime publishAt;
    private LocalDateTime expireAt;
}
