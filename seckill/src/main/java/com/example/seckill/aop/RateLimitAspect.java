package com.example.seckill.aop;

//2026.6.17 余浩洋 限流切面，基于Redis固定窗口计数器
import com.example.seckill.annotation.RateLimit;
import com.example.seckill.utils.UserContext;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 1. 构建限流key: rate_limit:userId:methodName
        String userId = UserContext.getUserId();
        if (userId == null) {
            userId = "anonymous";
        }
        String methodName = joinPoint.getSignature().getName();
        String redisKey = rateLimit.key() + ":" + userId + ":" + methodName;

        // 2. 固定窗口计数器：INCR + 首次设置过期时间
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            long expireSeconds = rateLimit.timeUnit().toSeconds(rateLimit.period());
            stringRedisTemplate.expire(redisKey, expireSeconds, TimeUnit.SECONDS);
        }

        // 3. 超限拦截
        if (count != null && count > rateLimit.limit()) {
            HttpServletResponse response = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getResponse();
            if (response != null) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    "{\"code\":429,\"message\":\"" + rateLimit.message() + "\"}"
                );
            }
            return null;
        }

        // 4. 放行，执行目标方法
        return joinPoint.proceed();
    }
}
