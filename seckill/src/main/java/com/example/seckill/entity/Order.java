package com.example.seckill.entity;

//2026.6.1 余浩洋 订单实体类
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private int orderId;
    private int userId;
    private int productId;
    private String productName;  // 非DB字段，join查询时填充
}
