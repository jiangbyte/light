package io.jiangbyte.github.infrastructure.content.announcement.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jiangbyte.github.domain.content.announcement.adapter.repository.IAnnouncementRepository;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPublishedPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.infrastructure.content.announcement.persistence.dao.AnnouncementDao;
import io.jiangbyte.github.infrastructure.content.announcement.persistence.dao.po.AnnouncementPO;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 通知公告 MyBatis-Plus 仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class AnnouncementRepository implements IAnnouncementRepository {
    private final AnnouncementDao announcementDao;

    @Override
    public Optional<AnnouncementEntity> findById(long id) {
        return Optional.ofNullable(announcementDao.selectById(id)).map(this::toEntity);
    }

    @Override
    public Optional<AnnouncementEntity> findPublishedById(long id, LocalDateTime now) {
        LambdaQueryWrapper<AnnouncementPO> wrapper = publishedWrapper(now);
        wrapper.eq(AnnouncementPO::getId, id);
        return Optional.ofNullable(announcementDao.selectOne(wrapper)).map(this::toEntity);
    }

    @Override
    public List<AnnouncementEntity> findByIds(Collection<Long> ids) {
        return announcementDao.selectByIds(ids).stream().map(this::toEntity).toList();
    }

    @Override
    public PageResult<AnnouncementEntity> findPage(AnnouncementPageQuery query) {
        LambdaQueryWrapper<AnnouncementPO> wrapper = new LambdaQueryWrapper<>();
        if (query.keyword() != null) {
            wrapper.and(condition -> condition
                    .like(AnnouncementPO::getTitle, query.keyword())
                    .or().like(AnnouncementPO::getSummary, query.keyword()));
        }
        wrapper.eq(query.type() != null, AnnouncementPO::getType,
                        query.type() == null ? null : query.type().name())
                .eq(query.priority() != null, AnnouncementPO::getPriority,
                        query.priority() == null ? null : query.priority().name())
                .eq(query.status() != null, AnnouncementPO::getStatus,
                        query.status() == null ? null : query.status().name())
                .orderByDesc(AnnouncementPO::getUpdatedAt)
                .orderByDesc(AnnouncementPO::getId);

        Page<AnnouncementPO> result = announcementDao.selectPage(
                new Page<>(query.page(), query.pageSize()), wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toEntity).toList(),
                result.getTotal(), query.page(), query.pageSize());
    }

    @Override
    public PageResult<AnnouncementEntity> findPublishedPage(AnnouncementPublishedPageQuery query,
                                                            LocalDateTime now) {
        LambdaQueryWrapper<AnnouncementPO> wrapper = publishedWrapper(now);
        if (query.keyword() != null) {
            wrapper.and(condition -> condition
                    .like(AnnouncementPO::getTitle, query.keyword())
                    .or().like(AnnouncementPO::getSummary, query.keyword()));
        }
        wrapper.eq(query.type() != null, AnnouncementPO::getType,
                        query.type() == null ? null : query.type().name())
                .eq(query.priority() != null, AnnouncementPO::getPriority,
                        query.priority() == null ? null : query.priority().name())
                .orderByDesc(AnnouncementPO::getPublishAt)
                .orderByDesc(AnnouncementPO::getId);

        Page<AnnouncementPO> result = announcementDao.selectPage(
                new Page<>(query.page(), query.pageSize()), wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toEntity).toList(),
                result.getTotal(), query.page(), query.pageSize());
    }

    @Override
    public AnnouncementEntity insert(AnnouncementEntity announcement) {
        AnnouncementPO po = toPO(announcement);
        announcementDao.insert(po);
        return toEntity(po);
    }

    @Override
    public AnnouncementEntity update(AnnouncementEntity announcement) {
        AnnouncementPO po = toPO(announcement);
        LambdaUpdateWrapper<AnnouncementPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AnnouncementPO::getId, announcement.getId())
                .set(AnnouncementPO::getTitle, announcement.getTitle())
                .set(AnnouncementPO::getSummary, announcement.getSummary())
                .set(AnnouncementPO::getContent, announcement.getContent())
                .set(AnnouncementPO::getType, announcement.getType().name())
                .set(AnnouncementPO::getPriority, announcement.getPriority().name())
                .set(AnnouncementPO::getStatus, announcement.getStatus().name())
                .set(AnnouncementPO::getPublishAt, announcement.getPublishAt())
                .set(AnnouncementPO::getExpireAt, announcement.getExpireAt())
                .set(AnnouncementPO::getUpdatedBy, announcement.getUpdatedBy())
                .set(AnnouncementPO::getUpdatedAt, announcement.getUpdatedAt());
        announcementDao.update(null, wrapper);
        return toEntity(po);
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {
        announcementDao.deleteByIds(ids);
    }

    private LambdaQueryWrapper<AnnouncementPO> publishedWrapper(LocalDateTime now) {
        LambdaQueryWrapper<AnnouncementPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementPO::getStatus, AnnouncementStatus.PUBLISHED.name())
                .le(AnnouncementPO::getPublishAt, now)
                .and(condition -> condition.isNull(AnnouncementPO::getExpireAt)
                        .or().gt(AnnouncementPO::getExpireAt, now));
        return wrapper;
    }

    private AnnouncementEntity toEntity(AnnouncementPO po) {
        return new AnnouncementEntity(po.getId(), po.getTitle(), po.getSummary(), po.getContent(),
                AnnouncementType.valueOf(po.getType()), AnnouncementPriority.valueOf(po.getPriority()),
                AnnouncementStatus.valueOf(po.getStatus()), po.getPublishAt(), po.getExpireAt(),
                po.getCreatedBy(), po.getUpdatedBy(), po.getCreatedAt(), po.getUpdatedAt());
    }

    private AnnouncementPO toPO(AnnouncementEntity announcement) {
        AnnouncementPO po = new AnnouncementPO();
        po.setId(announcement.getId());
        po.setTitle(announcement.getTitle());
        po.setSummary(announcement.getSummary());
        po.setContent(announcement.getContent());
        po.setType(announcement.getType().name());
        po.setPriority(announcement.getPriority().name());
        po.setStatus(announcement.getStatus().name());
        po.setPublishAt(announcement.getPublishAt());
        po.setExpireAt(announcement.getExpireAt());
        po.setCreatedBy(announcement.getCreatedBy());
        po.setUpdatedBy(announcement.getUpdatedBy());
        po.setCreatedAt(announcement.getCreatedAt());
        po.setUpdatedAt(announcement.getUpdatedAt());
        return po;
    }
}
