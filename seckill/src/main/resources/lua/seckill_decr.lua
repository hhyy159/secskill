-- 2026.6.21 余浩洋 Redis Lua脚本：原子检查并扣减秒杀库存
-- KEYS[1]: 库存key，如 "seckill:stock:1"
-- 返回值: 1=扣减成功  0=库存不足  -1=key不存在(未开始/已结束)

local key = KEYS[1]
local stock = redis.call('get', key)

if not stock then
    return -1
end

if tonumber(stock) <= 0 then
    return 0
end

redis.call('decr', key)
return 1
