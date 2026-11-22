package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.infrastructure.account.auth.config.Sm2Settings;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Portal 应用 SM2 密钥配置。
 */
@ConfigurationProperties(prefix = "light.security.sm2.portal")
public class PortalSm2Properties extends Sm2Settings {
}
