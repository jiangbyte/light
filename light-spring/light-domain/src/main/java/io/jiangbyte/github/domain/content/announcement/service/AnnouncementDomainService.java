package io.jiangbyte.github.domain.content.announcement.service;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.domain.content.announcement.adapter.repository.IAnnouncementRepository;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPublishedPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 通知公告领域服务，集中维护内容校验、发布窗口和状态流转。
 */
@RequiredArgsConstructor
public class AnnouncementDomainService {
    private static final int TITLE_MAX_LENGTH = 120;
    private static final int SUMMARY_MAX_LENGTH = 500;
    private static final int CONTENT_MAX_LENGTH = 100_000;
    private static final int DELETE_MAX_SIZE = 100;

    private final IAnnouncementRepository repository;

    /**
     * 创建草稿公告。
     */
    public AnnouncementEntity create(String title, String summary, String content,
                                     AnnouncementType type, AnnouncementPriority priority,
                                     LocalDateTime publishAt, LocalDateTime expireAt, long actorId) {
        String normalizedTitle = validateTitle(title);
        String normalizedSummary = validateSummary(summary);
        String normalizedContent = validateContent(content);
        requireType(type);
        requirePriority(priority);
        requireActor(actorId);
        LocalDateTime normalizedPublishAt = truncate(publishAt);
        LocalDateTime normalizedExpireAt = truncate(expireAt);
        validateSchedule(normalizedPublishAt, normalizedExpireAt, false);

        LocalDateTime now = now();
        AnnouncementEntity announcement = new AnnouncementEntity(null, normalizedTitle, normalizedSummary,
                normalizedContent, type, priority, AnnouncementStatus.DRAFT, normalizedPublishAt,
                normalizedExpireAt, actorId, actorId, now, now);
        return repository.insert(announcement);
    }

    /**
     * 更新草稿或已发布公告；归档公告不可直接修改。
     */
    public AnnouncementEntity update(long id, String title, String summary, String content,
                                     AnnouncementType type, AnnouncementPriority priority,
                                     LocalDateTime publishAt, LocalDateTime expireAt, long actorId) {
        requirePositiveId(id);
        requireActor(actorId);
        AnnouncementEntity current = getRequired(id);
        requireEditable(current.getStatus());

        String normalizedTitle = validateTitle(title);
        String normalizedSummary = validateSummary(summary);
        String normalizedContent = validateContent(content);
        requireType(type);
        requirePriority(priority);
        LocalDateTime normalizedPublishAt = truncate(publishAt);
        LocalDateTime normalizedExpireAt = truncate(expireAt);
        validateSchedule(normalizedPublishAt, normalizedExpireAt,
                current.getStatus() == AnnouncementStatus.PUBLISHED);

        AnnouncementEntity updated = new AnnouncementEntity(id, normalizedTitle, normalizedSummary,
                normalizedContent, type, priority, current.getStatus(), normalizedPublishAt,
                normalizedExpireAt, current.getCreatedBy(), actorId, current.getCreatedAt(), now());
        return repository.update(updated);
    }

    /**
     * 发布草稿或重新发布归档公告。
     */
    public AnnouncementEntity publish(long id, LocalDateTime publishAt, LocalDateTime expireAt, long actorId) {
        requirePositiveId(id);
        requireActor(actorId);
        AnnouncementEntity current = getRequired(id);
        if (current.getStatus() == AnnouncementStatus.PUBLISHED) {
            throw conflict("公告已经发布，不能重复发布");
        }

        LocalDateTime effectivePublishAt = truncate(publishAt == null ? now() : publishAt);
        LocalDateTime normalizedExpireAt = truncate(expireAt);
        validateSchedule(effectivePublishAt, normalizedExpireAt, true);

        AnnouncementEntity published = new AnnouncementEntity(id, current.getTitle(), current.getSummary(),
                current.getContent(), current.getType(), current.getPriority(), AnnouncementStatus.PUBLISHED,
                effectivePublishAt, normalizedExpireAt, current.getCreatedBy(), actorId,
                current.getCreatedAt(), now());
        return repository.update(published);
    }

    /**
     * 归档已发布公告。
     */
    public AnnouncementEntity archive(long id, long actorId) {
        requirePositiveId(id);
        requireActor(actorId);
        AnnouncementEntity current = getRequired(id);
        if (current.getStatus() != AnnouncementStatus.PUBLISHED) {
            throw conflict("只有已发布公告可以归档");
        }

        AnnouncementEntity archived = new AnnouncementEntity(id, current.getTitle(), current.getSummary(),
                current.getContent(), current.getType(), current.getPriority(), AnnouncementStatus.ARCHIVED,
                current.getPublishAt(), current.getExpireAt(), current.getCreatedBy(), actorId,
                current.getCreatedAt(), now());
        return repository.update(archived);
    }

    /**
     * 物理删除公告；请求中任一 ID 不存在时拒绝整批删除。
     */
    public void delete(Collection<Long> ids) {
        List<Long> targetIds = normalizeIds(ids);
        List<AnnouncementEntity> announcements = repository.findByIds(targetIds);
        if (announcements.size() != targetIds.size()) {
            throw new BizException(ResponseCode.NOT_FOUND.getCode(), "部分公告不存在", 404);
        }
        repository.deleteByIds(targetIds);
    }

    public AnnouncementEntity detail(long id) {
        requirePositiveId(id);
        return getRequired(id);
    }

    public PageResult<AnnouncementEntity> page(AnnouncementPageQuery query) {
        if (query == null) {
            throw illegalParameter("公告分页查询条件不能为空");
        }
        return repository.findPage(query);
    }

    /**
     * 查询当前已生效公告详情。
     */
    public AnnouncementEntity publishedDetail(long id) {
        requirePositiveId(id);
        return repository.findPublishedById(id, now())
                .orElseThrow(() -> new BizException(ResponseCode.NOT_FOUND.getCode(), "公告不存在", 404));
    }

    /**
     * 查询当前已生效公告。
     */
    public PageResult<AnnouncementEntity> publishedPage(AnnouncementPublishedPageQuery query) {
        if (query == null) {
            throw illegalParameter("公告分页查询条件不能为空");
        }
        return repository.findPublishedPage(query, now());
    }

    private AnnouncementEntity getRequired(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BizException(ResponseCode.NOT_FOUND.getCode(),
                        "公告不存在: " + id, 404));
    }

    private void requireEditable(AnnouncementStatus status) {
        if (status == AnnouncementStatus.ARCHIVED) {
            throw conflict("已归档公告不可修改，请重新发布后再编辑");
        }
    }

    private void validateSchedule(LocalDateTime publishAt, LocalDateTime expireAt, boolean published) {
        if (published && publishAt == null) {
            throw illegalParameter("已发布公告必须包含发布时间");
        }
        if (expireAt != null && publishAt == null) {
            throw illegalParameter("设置过期时间前必须设置发布时间");
        }
        if (publishAt != null && expireAt != null && !expireAt.isAfter(publishAt)) {
            throw illegalParameter("过期时间必须晚于发布时间");
        }
    }

    private String validateTitle(String title) {
        String value = StrUtil.trimToNull(title);
        if (value == null) {
            throw illegalParameter("公告标题不能为空");
        }
        if (value.length() > TITLE_MAX_LENGTH) {
            throw illegalParameter("公告标题不能超过 120 个字符");
        }
        return value;
    }

    private String validateSummary(String summary) {
        String value = StrUtil.trimToNull(summary);
        if (value != null && value.length() > SUMMARY_MAX_LENGTH) {
            throw illegalParameter("公告摘要不能超过 500 个字符");
        }
        return value;
    }

    private String validateContent(String content) {
        if (StrUtil.isBlank(content)) {
            throw illegalParameter("公告正文不能为空");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw illegalParameter("公告正文不能超过 100000 个字符");
        }
        return content;
    }

    private void requireType(AnnouncementType type) {
        if (type == null) {
            throw illegalParameter("公告类型不能为空");
        }
    }

    private void requirePriority(AnnouncementPriority priority) {
        if (priority == null) {
            throw illegalParameter("公告优先级不能为空");
        }
    }

    private void requireActor(long actorId) {
        if (actorId <= 0) {
            throw illegalParameter("操作者 ID 必须大于 0");
        }
    }

    private void requirePositiveId(long id) {
        if (id <= 0) {
            throw illegalParameter("公告 ID 必须大于 0");
        }
    }

    private List<Long> normalizeIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw illegalParameter("公告 ID 不能为空");
        }
        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null) {
                throw illegalParameter("公告 ID 不能为空");
            }
            requirePositiveId(id);
            uniqueIds.add(id);
        }
        if (uniqueIds.size() > DELETE_MAX_SIZE) {
            throw illegalParameter("单次最多删除 100 个公告");
        }
        return List.copyOf(uniqueIds);
    }

    private LocalDateTime truncate(LocalDateTime value) {
        return value == null ? null : value.truncatedTo(ChronoUnit.MILLIS);
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    private BizException illegalParameter(String message) {
        return new BizException(ResponseCode.ILLEGAL_PARAMETER, message);
    }

    private BizException conflict(String message) {
        return new BizException(ResponseCode.CONFLICT.getCode(), message, 409);
    }
}
