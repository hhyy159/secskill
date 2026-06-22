package com.example.seckill.config;

//2026.6.1 余浩洋 Spring MVC配置类，注册拦截器和CORS跨域
import com.example.seckill.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")           // 拦截所有路径
                .excludePathPatterns("/login", "/register"); // 放行登录和注册
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")               // 允许所有路径跨域
                .allowedOriginPatterns("*")       // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")              // 允许所有请求头
                .allowCredentials(true);          // 允许携带凭证
    }
}
