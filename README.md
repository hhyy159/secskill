# ⚡ secskill — 轻量级秒杀系统

一个基于 **Spring Boot 4.0.6 + Vue 3** 的简单秒杀示例项目，从前端到后端完整实现秒杀的核心链路。


**解决的两个问题**

1. **如何保证不超卖** — 三层防御体系：Redis Lua 原子预扣（第一层）→ RocketMQ 异步落库削峰（第二层）→ DB `WHERE stock > 0` + 联合唯一索引（第三层）
2. **如何应对高并发** — AOP 限流控制入口流量 → Redis 单线程处理库存读写 → MQ 将同步压力转为异步处理

## 技术栈

| 层级 | 技术 |
|------|------|
| **后端框架** | Spring Boot |
| **持久层** | MyBatis (XML 映射) |
| **数据库** | MySQL |
| **缓存** | Redis (Lettuce) |
| **消息队列** | RocketMQ |
| **鉴权** | JWT (java-jwt) |
| **AOP** | AspectJ | 
| **前端框架** | Vue 3 (Composition API) | 
| **前端路由** | Vue Router |
| **HTTP 客户端** | Axios | 
| **构建工具** | Vite / Maven |
| **语言** | Java 17 / JavaScript | — |

## 秒杀核心链路

```
用户点击"立即秒杀"
  │
  ▼
[AOP 限流]  Redis 固定窗口计数 → 超限返回 429
  │
  ▼
[Redis Lua 脚本]  原子 check stock > 0 → DECR → 返回 1/0/-1
  │
  ▼
[RocketMQ Producer]  syncSend → seckill-order-topic
  │
  ▼
[RocketMQ Consumer]  异步监听
  │
  ▼
[@Transactional]  幂等检查 → DB 扣库存 → 插订单
  │
  ▼
[联合唯一索引]  (user_id, product_id) 兜底防重复
```
### 接口一览

| 方法 | 路径 | 鉴权 | 说明 |
|------|------|------|------|
| POST | `/register` | ✗ | 注册（返回 JWT） |
| POST | `/login` | ✗ | 登录（返回 JWT） |
| GET | `/product/list` | ✓ | 商品列表（含状态） |
| GET | `/product/detail` | ✓ | 商品详情 |
| POST | `/seckill` | ✓ | 秒杀下单（限流 10次/秒） |
| GET | `/order/my` | ✓ | 我的订单 |

## 防护体系

| 层级 | 机制 | 作用 |
|------|------|------|
| **入口** | `@RateLimit` AOP | 每用户每秒 10 次请求 |
| **缓存** | Redis Lua 原子脚本 | 单线程执行，读-判-写无竞态 |
| **削峰** | RocketMQ 异步消费 | 同步响应 → 异步落库，解耦流量 |
| **事务** | `@Transactional` | DB 扣库存 + 插订单原子执行 |
| **幂等** | 先查后插 + 唯一索引 | 同一用户+商品只生成一条订单 |
