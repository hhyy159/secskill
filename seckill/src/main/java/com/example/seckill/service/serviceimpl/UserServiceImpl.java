package com.example.seckill.service.serviceimpl;

import com.example.seckill.dao.Userdao;
import com.example.seckill.service.UserService;
import com.example.seckill.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//2026.5.28 余浩洋 首次创建于2026.5.28 注册功能
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Userdao userdao;
    @Override
    public boolean AddUser(String account, String password) {
        return userdao.AddUser(account,password);
    }
    @Override
    public User findByAccount(String account){
        return userdao.findByAccount(account);
    }
    @Override
    public User findByAccountAndPassword(String account, String password){
        return userdao.findByAccountAndPassword(account,password);
    }
}
