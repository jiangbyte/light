package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.infrastructure.account.auth.crypto.Sm2CryptoService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Portal 应用密码传输加密配置。
 */
@Configuration
@EnableConfigurationProperties(PortalSm2Properties.class)
public class PortalCryptoConfiguration {
    @Bean
    public IPasswordTransportCryptoPort passwordTransportCryptoPort(PortalSm2Properties properties) {
        return new Sm2CryptoService(properties);
    }
}
