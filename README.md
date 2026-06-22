# ⚡ secskill — 轻量级秒杀系统

一个基于 **Spring Boot 4.0.6 + Vue 3** 的高并发秒杀示例项目，从前端到后端完整实现秒杀核心链路。

## 项目难度

| 维度 | 等级 | 说明 |
|------|------|------|
| **并发处理** | ⭐⭐⭐⭐ | Redis Lua 原子扣库存 + RocketMQ 异步削峰 + DB 唯一索引兜底，三层防护防止超卖 |
| **技术广度** | ⭐⭐⭐⭐ | SSM → Spring Boot 4.x → MyBatis → Redis → RocketMQ → AOP → Vue 3，全链路打通 |
| **业务复杂度** | ⭐⭐⭐ | 用户注册/登录/JWT鉴权、商品状态时序计算、秒杀限流、异步下单、订单查询 |
| **适合人群** | 初中级→高级过渡 | 适合掌握了 SSM 基础，正在学习分布式/高并发技术的开发者 |

**核心难点聚焦在两个方面：**

1. **如何保证不超卖** — 三层防御体系：Redis Lua 原子预扣（第一层）→ RocketMQ 异步落库削峰（第二层）→ DB `WHERE stock > 0` + 联合唯一索引（第三层）
2. **如何应对高并发** — AOP 限流控制入口流量 → Redis 单线程处理库存读写 → MQ 将同步压力转为异步处理

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 4.0.6 |
| **持久层** | MyBatis (XML 映射) | 4.0.1 |
| **数据库** | MySQL | 8.0 |
| **缓存** | Redis (Lettuce) | — |
| **消息队列** | RocketMQ | 2.3.0 (starter) |
| **鉴权** | JWT (java-jwt) | 4.4.0 |
| **AOP** | AspectJ | 1.9.22 |
| **前端框架** | Vue 3 (Composition API) | 3.4+ |
| **前端路由** | Vue Router | 4.3+ |
| **HTTP 客户端** | Axios | 1.7+ |
| **构建工具** | Vite / Maven | 5.4+ |
| **语言** | Java 17 / JavaScript | — |

## 项目结构

```
seckill/
├── pom.xml                          # Maven 依赖
├── src/main/java/com/example/seckill/
│   ├── SeckillApplication.java      # 启动类
│   ├── annotation/
│   │   └── RateLimit.java           # @RateLimit 限流注解
│   ├── aop/
│   │   └── RateLimitAspect.java     # 限流 AOP 切面（Redis 固定窗口）
│   ├── config/
│   │   ├── WebConfig.java           # 拦截器注册 + CORS 跨域
│   │   └── StockPreloadRunner.java  # 启动时预热库存到 Redis
│   ├── controller/
│   │   ├── UserController.java      # /login, /register
│   │   ├── ProductController.java   # /product/list, /product/detail
│   │   ├── SeckillController.java   # POST /seckill（核心秒杀接口）
│   │   └── OrderController.java     # GET /order/my
│   ├── dao/                         # MyBatis Mapper 接口
│   ├── entity/                      # POJO + VO（Lombok @Data）
│   ├── interceptor/
│   │   └── LoginInterceptor.java    # JWT 登录拦截器
│   ├── mq/
│   │   ├── SeckillOrderProducer.java # RocketMQ 生产者
│   │   └── SeckillOrderConsumer.java # RocketMQ 消费者
│   ├── service/                     # 服务接口
│   │   └── serviceimpl/             # 服务实现
│   └── utils/
│       ├── Result.java              # 统一响应 {code, message, data}
│       ├── JavaJWTUtils.java        # JWT 签发/验证/解析
│       └── UserContext.java         # ThreadLocal 存储 user_id
├── src/main/resources/
│   ├── application.yml              # 数据源、Redis、RocketMQ 配置
│   ├── mapper/                      # MyBatis XML 映射
│   └── lua/
│       └── seckill_decr.lua         # Redis 原子扣库存脚本
└── frontend/                        # Vue 3 前端
    ├── src/
    │   ├── api/index.js             # Axios 封装 + JWT 拦截器
    │   ├── router/index.js          # 路由 + 导航守卫
    │   ├── views/
    │   │   ├── Login.vue            # 登录/注册页
    │   │   ├── ProductList.vue      # 商品列表（4 种状态标签）
    │   │   ├── ProductDetail.vue    # 商品详情 + 秒杀按钮
    │   │   └── MyOrders.vue         # 我的订单
    │   ├── App.vue                  # 导航栏
    │   └── style.css                # 全局样式
    └── vite.config.js               # Vite 代理配置
```

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

## 数据库

```sql
-- user 表
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- product 表
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100),
    description TEXT,
    stock INT DEFAULT 0,
    start_time DATETIME,
    end_time DATETIME
);

-- order 表 + 防重复唯一索引
CREATE TABLE `order` (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL
);
ALTER TABLE `order` ADD UNIQUE INDEX idx_user_product (user_id, product_id);
```

## 快速启动

### 前置依赖

- JDK 17+
- MySQL 8.0（建库 `shop`，执行上述 DDL）
- Redis（默认 localhost:6379）
- RocketMQ（默认 localhost:9876，需先启动 NameServer + Broker）
- Node.js 18+（前端）

### 后端

```bash
cd seckill
./mvnw spring-boot:run
# 启动后自动预热库存到 Redis，控制台输出 [预加载] seckill:stock:1 = 100
```

### 前端

```bash
cd seckill/frontend
npm install
npm run dev
# 打开 http://localhost:3000
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
