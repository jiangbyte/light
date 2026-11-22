package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.infrastructure.account.auth.config.Sm2Settings;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Admin 应用 SM2 密钥配置。
 */
@ConfigurationProperties(prefix = "light.security.sm2.admin")
public class AdminSm2Properties extends Sm2Settings {
}
