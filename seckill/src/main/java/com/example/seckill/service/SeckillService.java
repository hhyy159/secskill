package com.example.seckill.service;

//2026.6.13 余浩洋 秒杀服务接口
public interface SeckillService {

    // 执行秒杀核心逻辑，返回: 1=成功排队  0=库存不足  -1=秒杀未开始/已结束
    int executeSeckill(String userId, int productId);
}
