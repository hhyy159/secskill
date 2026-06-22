package com.example.seckill.mq;

//2026.6.10 余浩洋 秒杀订单消息消费者，异步处理DB下单
import com.example.seckill.service.OrderService;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(
    topic = "seckill-order-topic",
    consumerGroup = "seckill-consumer-group",
    selectorExpression = "*",
    consumeMode = ConsumeMode.CONCURRENTLY,
    maxReconsumeTimes = 3
)
public class SeckillOrderConsumer implements RocketMQListener<Map<String, Object>> {

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(Map<String, Object> message) {
        String userId = (String) message.get("userId");
        Object productIdObj = message.get("productId");

        int productId;
        if (productIdObj instanceof Integer) {
            productId = (Integer) productIdObj;
        } else {
            productId = Integer.parseInt(productIdObj.toString());
        }

        System.out.println("[消费者] 收到下单消息 userId=" + userId + " productId=" + productId);

        // 调用事务方法完成：幂等检查 → DB扣库存 → 插入订单
        orderService.createOrder(userId, productId);

        System.out.println("[消费者] 下单完成 userId=" + userId + " productId=" + productId);
    }
}
