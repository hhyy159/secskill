package com.example.seckill.service.serviceimpl;

//2026.6.10 余浩洋 商品服务实现类
import com.example.seckill.dao.ProductDao;
import com.example.seckill.entity.Product;
import com.example.seckill.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Product findById(int product_id) {
        return productDao.findById(product_id);
    }
}
