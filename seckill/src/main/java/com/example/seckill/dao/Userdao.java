//2026.5.28 余浩洋
package com.example.seckill.dao;


import com.example.seckill.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Userdao {
    //2026.5.28 余浩洋 首次创建于2026.5.28 注册功能
    public boolean AddUser(String account, String password);
    public User findByAccount(String account);
    public User findByAccountAndPassword(String account, String password);
}
