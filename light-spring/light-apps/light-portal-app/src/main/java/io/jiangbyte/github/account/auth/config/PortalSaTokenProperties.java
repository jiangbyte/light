package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.infrastructure.account.auth.config.SaTokenSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Portal 应用的 Sa-Token 配置。
 */
@ConfigurationProperties(prefix = "light.sa-token.portal")
public class PortalSaTokenProperties extends SaTokenSettings {
}
