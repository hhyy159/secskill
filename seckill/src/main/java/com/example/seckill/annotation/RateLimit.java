package com.example.seckill.annotation;

//2026.6.17 余浩洋 限流注解
import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    // 限流key前缀
    String key() default "rate_limit";

    // 时间窗口内允许的最大请求数
    int limit() default 10;

    // 时间窗口长度
    long period() default 1;

    // 时间单位
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // 限流提示信息
    String message() default "请求过于频繁，请稍后重试";
}
