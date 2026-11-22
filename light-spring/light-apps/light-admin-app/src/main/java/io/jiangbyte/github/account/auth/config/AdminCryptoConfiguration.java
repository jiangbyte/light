package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.infrastructure.account.auth.crypto.Sm2CryptoService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Admin 应用密码传输加密配置。
 */
@Configuration
@EnableConfigurationProperties(AdminSm2Properties.class)
public class AdminCryptoConfiguration {
    @Bean
    public IPasswordTransportCryptoPort passwordTransportCryptoPort(AdminSm2Properties properties) {
        return new Sm2CryptoService(properties);
    }
}
