package io.jiangbyte.github.infrastructure.content.announcement.persistence.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告数据库持久化对象。
 */
@Data
@TableName("light_content_announcement")
public class AnnouncementPO {
    @TableId(type = IdType.AUTO)
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
