package com.example.seckill.config;

//2026.6.14 余浩洋 应用启动时预加载商品库存到Redis
import com.example.seckill.dao.ProductDao;
import com.example.seckill.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StockPreloadRunner implements ApplicationRunner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductDao productDao;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";

    @Override
    public void run(ApplicationArguments args) {
        List<Product> products = productDao.findAll();
        for (Product product : products) {
            String key = STOCK_KEY_PREFIX + product.getProductId();
            // 只预热未结束的活动商品
            if (LocalDateTime.now().isBefore(product.getEndTime())) {
                stringRedisTemplate.opsForValue().set(key, String.valueOf(product.getStock()));
                System.out.println("[预加载] " + key + " = " + product.getStock());
            }
        }
        System.out.println("[预加载] 商品库存预热完成，共加载 " + products.size() + " 个商品");
    }
}
