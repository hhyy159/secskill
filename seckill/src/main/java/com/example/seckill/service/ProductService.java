package com.example.seckill.service;

//2026.6.10 余浩洋 商品服务接口
import com.example.seckill.entity.Product;

import java.util.List;

public interface ProductService {

    // 查询所有商品列表
    List<Product> findAll();

    // 根据ID查询商品详情
    Product findById(int product_id);
}
