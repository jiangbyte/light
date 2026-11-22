package io.jiangbyte.github.infrastructure.account.auth.config;

import io.jiangbyte.github.domain.account.adapter.port.IAccountPasswordPort;
import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.adapter.repository.IAccountRepository;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.infrastructure.account.auth.adapter.BCryptAccountPasswordAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 账户领域端口的基础设施装配。
 */
@Configuration
public class AccountInfrastructureConfiguration {
    @Bean
    public IAccountPasswordPort accountPasswordPort() {
        return new BCryptAccountPasswordAdapter();
    }

    @Bean
    public AccountDomainService accountDomainService(IAccountRepository repository,
                                                     IAccountPasswordPort passwordPort,
                                                     IAccountSessionPort sessionPort) {
        return new AccountDomainService(repository, passwordPort, sessionPort);
    }
}
