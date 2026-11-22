package io.jiangbyte.github.config;

import io.jiangbyte.github.trigger.portal.account.auth.http.PortalAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Portal HTTP 入站约束：仅允许 GET/POST，并保护除登录外的所有 Portal 路径。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration extends AuthenticatedWebMvcConfiguration {
    private final PortalAuthenticationInterceptor authenticationInterceptor;

    @Override
    protected HandlerInterceptor authenticationInterceptor() {
        return authenticationInterceptor;
    }
}
