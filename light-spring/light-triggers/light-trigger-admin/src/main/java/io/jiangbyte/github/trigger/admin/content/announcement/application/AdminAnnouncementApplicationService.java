package io.jiangbyte.github.trigger.admin.content.announcement.application;

import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPageQuery;
import io.jiangbyte.github.domain.content.announcement.service.AnnouncementDomainService;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.CreateAnnouncementCommand;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.PublishAnnouncementCommand;
import io.jiangbyte.github.trigger.admin.content.announcement.application.command.UpdateAnnouncementCommand;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Admin 通知公告管理用例。
 */
@Service
@RequiredArgsConstructor
public class AdminAnnouncementApplicationService {
    private final AnnouncementDomainService announcementDomainService;
    private final AccountDomainService accountDomainService;

    public PageResult<AnnouncementEntity> page(AnnouncementPageQuery query) {
        currentAdmin();
        return announcementDomainService.page(query);
    }

    public AnnouncementEntity detail(long id) {
        currentAdmin();
        return announcementDomainService.detail(id);
    }

    public AnnouncementEntity create(CreateAnnouncementCommand command) {
        long actorId = currentAdmin().getId();
        return announcementDomainService.create(command.getTitle(), command.getSummary(),
                command.getContent(), command.getType(), command.getPriority(),
                command.getPublishAt(), command.getExpireAt(), actorId);
    }

    public AnnouncementEntity update(UpdateAnnouncementCommand command) {
        long actorId = currentAdmin().getId();
        return announcementDomainService.update(command.getId(), command.getTitle(), command.getSummary(),
                command.getContent(), command.getType(), command.getPriority(),
                command.getPublishAt(), command.getExpireAt(), actorId);
    }

    public AnnouncementEntity publish(PublishAnnouncementCommand command) {
        long actorId = currentAdmin().getId();
        return announcementDomainService.publish(command.getId(),
                command.getPublishAt(), command.getExpireAt(), actorId);
    }

    public AnnouncementEntity archive(long id) {
        long actorId = currentAdmin().getId();
        return announcementDomainService.archive(id, actorId);
    }

    public void delete(List<Long> ids) {
        currentAdmin();
        announcementDomainService.delete(ids);
    }

    private AccountEntity currentAdmin() {
        return accountDomainService.current(AccountType.ADMIN);
    }
}
