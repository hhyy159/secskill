package com.example.seckill.mq;

//2026.6.10 余浩洋 秒杀订单消息生产者
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SeckillOrderProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String TOPIC = "seckill-order-topic";

    /**
     * 发送秒杀订单消息到RocketMQ
     * @param userId 用户ID
     * @param productId 商品ID
     * @return true=发送成功 false=发送失败
     */
    public boolean sendOrderMessage(String userId, int productId) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", userId);
        msg.put("productId", productId);

        try {
            SendResult result = rocketMQTemplate.syncSend(TOPIC, msg);
            return SendStatus.SEND_OK.equals(result.getSendStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
