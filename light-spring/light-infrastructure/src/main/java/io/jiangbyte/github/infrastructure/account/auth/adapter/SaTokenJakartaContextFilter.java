package io.jiangbyte.github.infrastructure.account.auth.adapter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 为 Sa-Token 1.44 显式桥接 Boot 4 使用的 Jakarta Servlet 上下文。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SaTokenJakartaContextFilter implements Filter {
    private final SaTokenContextForThreadLocal context = new SaTokenContextForThreadLocal();

    public SaTokenJakartaContextFilter() {
        SaManager.setSaTokenContext(context);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1. 将当前 Jakarta 请求绑定到 Sa-Token 线程上下文
        context.setContext(new SaRequestForServlet(request), new SaResponseForServlet(response),
                new SaStorageForServlet(request));
        try {
            // 2. 放行后续鉴权与业务处理
            filterChain.doFilter(request, response);
        } finally {
            // 3. 无论成功失败都清理线程上下文，避免线程复用串号
            context.clearContext();
        }
    }
}
