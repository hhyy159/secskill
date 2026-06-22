package com.example.seckill.interceptor;

//2026.5.29 余浩洋 登录拦截器，校验JWT Token并将user_id存入ThreadLocal
import com.example.seckill.utils.JavaJWTUtils;
import com.example.seckill.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放过OPTIONS预检请求（解决CORS跨域预检问题）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放过登录和注册接口
        String path = request.getRequestURI();
        if (path.contains("/login") || path.contains("/register")) {
            return true;
        }

        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // 校验token有效性
        if (!JavaJWTUtils.verifyToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期，请重新登录\"}");
            return false;
        }

        // 解析user_id并存入ThreadLocal，供后续业务使用
        String userId = JavaJWTUtils.parseToken(token);
        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理ThreadLocal，防止内存泄漏
        UserContext.remove();
    }
}
