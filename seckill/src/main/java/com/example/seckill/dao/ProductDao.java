package com.example.seckill.dao;

//2026.6.10 余浩洋 商品Mapper接口
import com.example.seckill.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDao {

    // 查询所有商品
    List<Product> findAll();

    // 根据ID查询商品
    Product findById(int product_id);

    // 减库存（实际扣减，带版本号或行锁由SQL控制）
    int updateStock(int product_id, int stock);

    // 原子扣减库存（SQL层面 WHERE stock > 0 防超卖）
    int decrementStock(int product_id);
}
