package com.feishu.blog.config;

import com.feishu.blog.interceptor.AccessLogInterceptor;
import com.feishu.blog.interceptor.AccessTokenInterceptor;
import com.feishu.blog.interceptor.RefreshTokenInterceptor;
import com.feishu.blog.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/blog/**",
                        "/files/img/**",
                        "/api/user/check_email/**",
                        "/api/user/check_username/**",
                        "/api/captcha/**"
                ); // 白名单 (可选); 按需排除静态资源等
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/api/auth/fresh");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // ← 注意这里
        // 访问路径：/files/img/xxx.jpg → 映射到磁盘 IMAGE_SAVE_DIR
        registry.addResourceHandler("/files/img/**")
                .addResourceLocations("file:" + FileUtil.IMAGE_SAVE_DIR)   // 必须加 file:
                .setCachePeriod(3600);                     // 浏览器缓存 1h，可选
    }
}
