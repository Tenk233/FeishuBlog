package com.feishu.blog.config;

import com.feishu.blog.interceptor.AccessLogInterceptor;
import com.feishu.blog.interceptor.JwtInterceptor;
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
@RequiredArgsConstructor          // Lombok 注入 3 个拦截器
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AccessLogInterceptor accessLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/error"); // 白名单 (可选); 按需排除静态资源等
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**");
    }
}
