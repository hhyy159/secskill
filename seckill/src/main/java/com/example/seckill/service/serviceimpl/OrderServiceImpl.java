package com.example.seckill.service.serviceimpl;

//2026.6.11 余浩洋 订单服务实现类
import com.example.seckill.dao.OrderDao;
import com.example.seckill.dao.ProductDao;
import com.example.seckill.entity.Order;
import com.example.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Override
    @Transactional
    public void createOrder(String userId, int productId) {
        // 1. 幂等性检查：已存在的订单直接返回
        Order existing = orderDao.findByUserIdAndProductId(
                Integer.parseInt(userId), productId);
        if (existing != null) {
            return;
        }

        // 2. 原子扣减DB库存（SQL层 WHERE stock > 0 保证不超卖）
        int rows = productDao.decrementStock(productId);
        if (rows == 0) {
            throw new RuntimeException("DB库存不足，productId=" + productId);
        }

        // 3. 插入订单（联合唯一索引兜底）
        orderDao.InsertOrder(Integer.parseInt(userId), productId);
    }

    @Override
    public List<Order> findByUserId(int userId) {
        return orderDao.findByUserId(userId);
    }
}
