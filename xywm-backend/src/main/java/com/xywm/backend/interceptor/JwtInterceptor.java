package com.xywm.backend.interceptor;

import com.xywm.backend.common.BusinessException;
import com.xywm.backend.utils.JwtUtils;
import com.xywm.backend.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(401, "请先登录");
        }

        token = token.substring(7);
        try {
            Claims claims = jwtUtils.parseToken(token);
            // 将解析出的用户信息存入你写好的 UserContext
            UserContext.setUserId(claims.get("userId", Long.class));
            UserContext.setUserRole(claims.get("role", Integer.class));
            return true;
        } catch (Exception e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束清理线程数据，防止内存泄漏
        UserContext.clear();
    }
}