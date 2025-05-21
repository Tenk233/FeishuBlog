package com.feishu.blog.config;

import com.feishu.blog.interceptor.AccessLogInterceptor;
import com.feishu.blog.interceptor.AccessTokenInterceptor;
import com.feishu.blog.interceptor.RefreshTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/15
 */
@Configuration
@RequiredArgsConstructor          // Lombok 注入拦截器
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessTokenInterceptor accessTokenInterceptor;
    private final AccessLogInterceptor accessLogInterceptor;
    private final RefreshTokenInterceptor refreshTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(accessTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/**"); // 白名单 (可选); 按需排除静态资源等
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/api/auth/fresh");
    }
}
