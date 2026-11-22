package io.jiangbyte.github.domain.content.announcement.service;

import io.jiangbyte.github.domain.content.announcement.adapter.repository.IAnnouncementRepository;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.types.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementDomainServiceTest {
    @Mock
    private IAnnouncementRepository repository;

    private AnnouncementDomainService service;

    @BeforeEach
    void setUp() {
        service = new AnnouncementDomainService(repository);
    }

    @Test
    void createBuildsDraftWithActorAndNormalizedTitle() {
        when(repository.insert(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AnnouncementEntity created = service.create("  系统通知  ", "摘要", "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.IMPORTANT, null, null, 7L);

        assertThat(created.getTitle()).isEqualTo("系统通知");
        assertThat(created.getStatus()).isEqualTo(AnnouncementStatus.DRAFT);
        assertThat(created.getCreatedBy()).isEqualTo(7L);
        assertThat(created.getUpdatedBy()).isEqualTo(7L);
    }

    @Test
    void expireTimeMustBeAfterPublishTime() {
        LocalDateTime publishAt = LocalDateTime.of(2026, 1, 2, 10, 0);

        assertThatThrownBy(() -> service.create("通知", null, "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.NORMAL,
                publishAt, publishAt.minusMinutes(1), 7L))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0400");
    }

    @Test
    void archivedAnnouncementCanBePublishedAgain() {
        AnnouncementEntity archived = announcement(AnnouncementStatus.ARCHIVED);
        when(repository.findById(11L)).thenReturn(Optional.of(archived));
        when(repository.update(any())).thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime publishAt = LocalDateTime.of(2026, 2, 1, 9, 0);

        AnnouncementEntity published = service.publish(11L, publishAt, null, 9L);

        assertThat(published.getStatus()).isEqualTo(AnnouncementStatus.PUBLISHED);
        assertThat(published.getPublishAt()).isEqualTo(publishAt);
        assertThat(published.getUpdatedBy()).isEqualTo(9L);
    }

    @Test
    void archivedAnnouncementCannotBeEditedDirectly() {
        when(repository.findById(11L)).thenReturn(Optional.of(announcement(AnnouncementStatus.ARCHIVED)));

        assertThatThrownBy(() -> service.update(11L, "标题", null, "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.NORMAL, null, null, 9L))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0409");
    }

    @Test
    void batchDeleteRejectsMissingAnnouncement() {
        when(repository.findByIds(List.of(1L, 2L)))
                .thenReturn(List.of(announcement(AnnouncementStatus.DRAFT)));

        assertThatThrownBy(() -> service.delete(List.of(1L, 2L)))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0404");
    }

    @Test
    void batchDeleteNormalizesDuplicateIds() {
        when(repository.findByIds(List.of(1L)))
                .thenReturn(List.of(announcement(AnnouncementStatus.DRAFT)));

        service.delete(List.of(1L, 1L));

        verify(repository).deleteByIds(List.of(1L));
    }

    private AnnouncementEntity announcement(AnnouncementStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new AnnouncementEntity(11L, "标题", "摘要", "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.NORMAL, status,
                status == AnnouncementStatus.DRAFT ? null : now.minusHours(1),
                null, 1L, 1L, now.minusDays(1), now);
    }
}
