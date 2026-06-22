package com.example.seckill.dao;

//2026.6.11 余浩洋 订单Mapper接口
import com.example.seckill.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {

    // 插入订单
    int InsertOrder(int user_id, int product_id);

    // 根据用户和商品查询订单（幂等性检查）
    Order findByUserIdAndProductId(int user_id, int product_id);

    // 根据用户ID查询所有订单（关联商品名）
    java.util.List<Order> findByUserId(int user_id);
}
