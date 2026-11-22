package io.jiangbyte.github.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Admin/Portal 共用的 HTTP 入站约束。
 */
public abstract class AuthenticatedWebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 先限制请求方法，再保护当前应用自己的 /api 路径。
     */
    @Override
    public final void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GetOrPostOnlyInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login/**", "/api/auth/transport-key");
    }

    /**
     * 由具体应用提供自己的登录域拦截器。
     */
    protected abstract HandlerInterceptor authenticationInterceptor();

    /**
     * 将非 GET/POST 请求统一转换为 405。
     */
    private static final class GetOrPostOnlyInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws HttpRequestMethodNotSupportedException {
            String method = request.getMethod();
            if (!"GET".equals(method) && !"POST".equals(method)) {
                throw new HttpRequestMethodNotSupportedException(method, List.of("GET", "POST"));
            }
            return true;
        }
    }
}
