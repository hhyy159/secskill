package com.example.seckill.controller;

//2026.6.22 余浩洋 订单控制器
import com.example.seckill.entity.Order;
import com.example.seckill.service.OrderService;
import com.example.seckill.utils.Result;
import com.example.seckill.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 我的订单列表
     * 从UserContext获取当前登录用户ID，查询其所有秒杀订单
     */
    @GetMapping("/my")
    public Result<List<Order>> myOrders() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }

        List<Order> orders = orderService.findByUserId(Integer.parseInt(userId));
        return Result.success(orders);
    }
}
