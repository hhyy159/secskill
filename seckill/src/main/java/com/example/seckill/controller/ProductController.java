package com.example.seckill.controller;

//2026.6.12 余浩洋 秒杀商品控制器
import com.example.seckill.entity.Product;
import com.example.seckill.entity.ProductVO;
import com.example.seckill.service.ProductService;
import com.example.seckill.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 秒杀商品列表展示
     * 根据当前时间与start_time/end_time/stock，返回每个商品的状态
     */
    @GetMapping("/list")
    public Result<List<ProductVO>> list() {
        List<Product> products = productService.findAll();
        List<ProductVO> voList = products.stream()
                .map(ProductVO::fromProduct)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 秒杀商品详情
     */
    @GetMapping("/detail")
    public Result<ProductVO> detail(@RequestParam("product_id") int product_id) {
        Product product = productService.findById(product_id);
        if (product == null) {
            return Result.fail(404, "商品不存在");
        }
        return Result.success(ProductVO.fromProduct(product));
    }
}
