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

            Object userIdObj = claims.get("userId");
            if (userIdObj == null) userIdObj = claims.get("id"); // 兼容获取

            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                UserContext.setUserId(userId); // 保留旧的缓存用法

                // 【终极防丢手段】直接把 ID 钉在本次 Request 请求对象上！
                request.setAttribute("currentUserId", userId);

                Object roleObj = claims.get("role");
                if (roleObj != null) {
                    UserContext.setUserRole(Integer.valueOf(roleObj.toString()));
                }
            } else {
                throw new BusinessException(401, "Token无效：无法提取用户ID");
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}