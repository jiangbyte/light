package io.jiangbyte.github.domain.content.announcement.adapter.repository;

import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPublishedPageQuery;
import io.jiangbyte.github.types.page.PageResult;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 通知公告聚合仓储端口。
 */
public interface IAnnouncementRepository {
    Optional<AnnouncementEntity> findById(long id);

    Optional<AnnouncementEntity> findPublishedById(long id, LocalDateTime now);

    List<AnnouncementEntity> findByIds(Collection<Long> ids);

    PageResult<AnnouncementEntity> findPage(AnnouncementPageQuery query);

    PageResult<AnnouncementEntity> findPublishedPage(AnnouncementPublishedPageQuery query, LocalDateTime now);

    AnnouncementEntity insert(AnnouncementEntity announcement);

    AnnouncementEntity update(AnnouncementEntity announcement);

    void deleteByIds(Collection<Long> ids);
}
