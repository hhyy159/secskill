package com.example.seckill.entity;

//2026.5.30 余浩洋 秒杀商品实体类
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int productId;
    private String productName;
    private String description;
    private int stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
