package com.example.seckill.service;

//2026.6.11 余浩洋 订单服务接口
public interface OrderService {

    // 创建订单（含幂等性检查 + DB库存扣减 + 插入订单）
    void createOrder(String userId, int productId);

    // 根据用户ID查询订单列表
    java.util.List<com.example.seckill.entity.Order> findByUserId(int userId);
}
