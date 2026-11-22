package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Admin 应用独占的 Sa-Token 装配。
 */
@Configuration
@EnableConfigurationProperties(AdminSaTokenProperties.class)
public class AdminSaTokenConfiguration {
    @Bean
    public IAccountSessionPort accountSessionPort(AdminSaTokenProperties properties,
                                                  RedisConnectionFactory connectionFactory) {
        return SaTokenAccountSessionFactory.create(properties, connectionFactory, AccountType.ADMIN);
    }
}
