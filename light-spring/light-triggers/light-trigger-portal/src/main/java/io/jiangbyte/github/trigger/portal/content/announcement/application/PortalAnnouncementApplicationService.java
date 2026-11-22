package io.jiangbyte.github.trigger.portal.content.announcement.application;

import io.jiangbyte.github.domain.content.announcement.model.entity.AnnouncementEntity;
import io.jiangbyte.github.domain.content.announcement.model.valobj.AnnouncementPublishedPageQuery;
import io.jiangbyte.github.domain.content.announcement.service.AnnouncementDomainService;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Portal 已发布通知公告查询用例。
 */
@Service
@RequiredArgsConstructor
public class PortalAnnouncementApplicationService {
    private final AnnouncementDomainService announcementDomainService;

    public PageResult<AnnouncementEntity> publishedPage(AnnouncementPublishedPageQuery query) {
        return announcementDomainService.publishedPage(query);
    }

    public AnnouncementEntity publishedDetail(long id) {
        return announcementDomainService.publishedDetail(id);
    }
}
