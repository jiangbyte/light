package io.jiangbyte.github.trigger.admin.content.announcement.application;

import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPriority;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementStatus;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementType;
import io.jiangbyte.github.domain.content.announcement.service.AnnouncementDomainService;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.CreateAnnouncementCommand;
import io.jiangbyte.github.types.page.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAnnouncementApplicationServiceTest {
    @Mock
    private AnnouncementDomainService announcementDomainService;
    @Mock
    private AccountDomainService accountDomainService;

    private AdminAnnouncementApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AdminAnnouncementApplicationService(announcementDomainService, accountDomainService);
    }

    @Test
    void createUsesCurrentAdminAsActor() {
        AccountEntity admin = admin();
        CreateAnnouncementCommand command = new CreateAnnouncementCommand(
                "通知", "摘要", "正文", AnnouncementType.NOTICE, AnnouncementPriority.NORMAL, null, null);
        AnnouncementEntity expected = announcement();
        when(accountDomainService.current(AccountType.ADMIN)).thenReturn(admin);
        when(announcementDomainService.create("通知", "摘要", "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.NORMAL, null, null, 7L))
                .thenReturn(expected);

        AnnouncementEntity result = service.create(command);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void adminPageChecksCurrentAdmin() {
        AccountEntity admin = admin();
        AnnouncementPageQuery query =
                new AnnouncementPageQuery(1, 20, null, null, null, null);
        PageResult<AnnouncementEntity> expected = new PageResult<>(List.of(), 0, 1, 20);
        when(accountDomainService.current(AccountType.ADMIN)).thenReturn(admin);
        when(announcementDomainService.page(query)).thenReturn(expected);

        PageResult<AnnouncementEntity> result = service.page(query);

        assertThat(result).isSameAs(expected);
        verify(announcementDomainService).page(query);
    }

    private AccountEntity admin() {
        LocalDateTime now = LocalDateTime.now();
        return new AccountEntity(7L, "admin", "hash", "管理员", null, null,
                AccountStatus.ACTIVE, 0, now, now);
    }

    private AnnouncementEntity announcement() {
        LocalDateTime now = LocalDateTime.now();
        return new AnnouncementEntity(11L, "通知", "摘要", "正文",
                AnnouncementType.NOTICE, AnnouncementPriority.NORMAL, AnnouncementStatus.DRAFT,
                null, null, 7L, 7L, now, now);
    }
}
