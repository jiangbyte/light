package io.jiangbyte.github.config;

import io.jiangbyte.github.trigger.admin.account.auth.http.AdminAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理端 HTTP 入站约束：仅允许 GET/POST，并保护除登录外的所有 Admin 路径。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration extends AuthenticatedWebMvcConfiguration {
    private final AdminAuthenticationInterceptor authenticationInterceptor;

    @Override
    protected HandlerInterceptor authenticationInterceptor() {
        return authenticationInterceptor;
    }
}
