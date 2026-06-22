package com.example.seckill.service.serviceimpl;

//2026.6.13 余浩洋 秒杀服务实现类
import com.example.seckill.mq.SeckillOrderProducer;
import com.example.seckill.service.SeckillService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SeckillOrderProducer seckillOrderProducer;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";

    private DefaultRedisScript<Long> seckillScript;

    @PostConstruct
    public void init() {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setLocation(new ClassPathResource("lua/seckill_decr.lua"));
        seckillScript.setResultType(Long.class);
    }

    /**
     * 秒杀核心流程：
     * 1. Redis Lua脚本原子检查并扣减库存
     * 2. 扣减成功后发送MQ消息异步创建订单
     * @return 1-成功排队  0-库存不足  -1-秒杀未开始/已结束
     */
    @Override
    public int executeSeckill(String userId, int productId) {
        // Step 1: 原子扣减Redis库存
        String stockKey = STOCK_KEY_PREFIX + productId;
        List<String> keys = Collections.singletonList(stockKey);
        Long result = stringRedisTemplate.execute(seckillScript, keys);

        int code = result != null ? result.intValue() : -1;

        if (code == 1) {
            // Step 2: Redis扣减成功，发送MQ消息异步下单
            boolean sent = seckillOrderProducer.sendOrderMessage(userId, productId);
            if (!sent) {
                throw new RuntimeException("消息队列发送失败，请稍后重试");
            }
        }

        return code;
    }
}
