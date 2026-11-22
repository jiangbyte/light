package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.infrastructure.account.auth.config.SaTokenSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Admin 应用的 Sa-Token 配置。
 */
@ConfigurationProperties(prefix = "light.sa-token.admin")
public class AdminSaTokenProperties extends SaTokenSettings {
}
