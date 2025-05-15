package com.feishu.blog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 简单访问日志：请求 URL + Query 参数
 */
@Slf4j
@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse rsp,
                             Object handler) {

        String url   = req.getRequestURL().toString();
        String query = req.getQueryString();          // 可能为 null
        String full  = query == null ? url : url + "?" + query;

        log.info(">> {}", full);                     // 这里就是打印 URL
        return true;                                 // 放行
    }
}
