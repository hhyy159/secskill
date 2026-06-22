package com.example.seckill.controller;

import com.example.seckill.entity.User;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.JavaJWTUtils;
import com.example.seckill.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userservice;

@PostMapping("/register")
//2026.5.28 余浩洋 首次创建于2026.5.28 注册功能
    public Result<String> Register(
        @RequestParam("account") String account,
        @RequestParam("password") String password
    )
    {
        //账号是否存在 2026.5.30 修改
        boolean isCun = userservice.findByAccount(account) != null;
        if(isCun)
        {
            return Result.fail("账号已存在");
        }
        //是否注册成功 2026.5.30 修改
        boolean isSuccess = userservice.AddUser(account,password);
        if(!isSuccess)
        {
            return Result.fail("注册失败");
        }
        User newuser = userservice.findByAccount(account);
        String jwt =  JavaJWTUtils.createToken(String.valueOf(newuser.getUserId()),newuser.getAccount(),newuser.getPassword());
        return new Result<String>(jwt,true,"注册成功");
    }
@PostMapping("/login")
//2026.5.30 余浩洋 首次创建于2026.5.30 登录功能
    public Result<String> Login(
        @RequestParam("account") String account,
        @RequestParam("password") String password
    )
    {
        //账号是否存在 2026.5.30 修改
        boolean notCun = userservice.findByAccount(account) == null;
        if(notCun)
        {
            return Result.fail("账号不存在");
        }
        //密码是否正确 2026.5.30 修改
        boolean isCorrect = userservice.findByAccountAndPassword(account,password) != null;
        if(!isCorrect)
        {
            return Result.fail("密码错误");
        }
        User newuser = userservice.findByAccount(account);
        String jwt =  JavaJWTUtils.createToken(String.valueOf(newuser.getUserId()),newuser.getAccount(),newuser.getPassword());
        return new Result<String>(jwt,true,"登录成功");
    }


}

