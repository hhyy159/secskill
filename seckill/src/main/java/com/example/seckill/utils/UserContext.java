package com.example.seckill.utils;

//2026.6.21 余浩洋 ThreadLocal工具类，用于在请求上下文中存储当前登录用户的user_id
public class UserContext {
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static void remove() {
        USER_ID.remove();
    }
}
