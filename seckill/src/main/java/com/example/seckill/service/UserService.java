package com.example.seckill.service;

import com.example.seckill.entity.User;

public interface UserService {
    //2026.5.28 余浩洋 首次创建于2026.5.28 注册功能
    boolean AddUser(String account, String password);
    User findByAccount(String account);
    User findByAccountAndPassword(String account, String password);
}
