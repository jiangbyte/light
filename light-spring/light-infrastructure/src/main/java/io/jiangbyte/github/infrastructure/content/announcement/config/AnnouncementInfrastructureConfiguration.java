package io.jiangbyte.github.infrastructure.content.announcement.config;

import io.jiangbyte.github.domain.content.announcement.adapter.repository.IAnnouncementRepository;
import io.jiangbyte.github.domain.content.announcement.service.AnnouncementDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通知公告领域服务装配。
 */
@Configuration
public class AnnouncementInfrastructureConfiguration {
    @Bean
    public AnnouncementDomainService announcementDomainService(IAnnouncementRepository repository) {
        return new AnnouncementDomainService(repository);
    }
}
