package com.example.seckill.controller;

//2026.6.13 余浩洋 秒杀控制器
import com.example.seckill.annotation.RateLimit;
import com.example.seckill.service.SeckillService;
import com.example.seckill.utils.Result;
import com.example.seckill.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀下单接口
     * 从UserContext获取当前登录用户，经过RateLimit限流后执行秒杀逻辑
     */
    @RateLimit(key = "seckill", limit = 10, period = 1, timeUnit = TimeUnit.SECONDS,
              message = "请求过于频繁，请稍后重试")
    @PostMapping
    public Result<String> seckill(@RequestParam("product_id") int productId) {
        // 从ThreadLocal获取当前登录用户ID
        String userId = UserContext.getUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }

        int code = seckillService.executeSeckill(userId, productId);

        switch (code) {
            case 1:
                return new Result<>("排队中", true, "秒杀成功，订单处理中");
            case 0:
                return Result.fail("库存不足，已售罄");
            case -1:
                return Result.fail("秒杀未开始或已结束");
            default:
                return Result.fail("未知错误");
        }
    }
}
