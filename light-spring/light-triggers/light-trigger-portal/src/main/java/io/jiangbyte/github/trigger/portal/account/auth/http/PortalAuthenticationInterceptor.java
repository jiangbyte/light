package io.jiangbyte.github.trigger.portal.account.auth.http;

import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Portal 统一鉴权入口，只接受 Portal 登录域令牌。
 */
@Component
@RequiredArgsConstructor
public class PortalAuthenticationInterceptor implements HandlerInterceptor {
    private final AccountDomainService accountDomainService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 校验 Portal 域令牌有效且账户仍处于 ACTIVE
        accountDomainService.current(AccountType.PORTAL);
        // 2. 放行已认证 Portal 请求
        return true;
    }
}
