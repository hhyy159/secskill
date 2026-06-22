<template>
  <div class="container">
    <h2 style="margin: 20px 0; font-size: 22px;">我的订单</h2>

    <!-- 加载中 -->
    <div v-if="loading" style="text-align: center; padding: 60px 0; color: #999;">
      加载中...
    </div>

    <!-- 空列表 -->
    <div v-else-if="orders.length === 0" class="card" style="text-align: center; padding: 60px 0; color: #999;">
      <p style="font-size: 16px; margin-bottom: 8px;">暂无订单</p>
      <router-link to="/products" class="btn btn-primary" style="margin-top: 12px;">去秒杀</router-link>
    </div>

    <!-- 订单列表 -->
    <div v-for="order in orders" :key="order.orderId" class="card order-card">
      <div class="order-header">
        <span class="order-id">订单号：{{ order.orderId }}</span>
        <span class="order-status">已秒杀</span>
      </div>
      <div class="order-body">
        <div class="order-info">
          <span class="info-label">商品名称</span>
          <span class="info-value">{{ order.productName || '商品' + order.productId }}</span>
        </div>
        <div class="order-info">
          <span class="info-label">商品ID</span>
          <span class="info-value">{{ order.productId }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../api'

const orders = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await orderApi.myOrders()
    if (res.code === 200) {
      orders.value = res.data || []
    }
  } catch (e) {
    console.error('获取订单列表失败', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-card {
  padding: 16px 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.order-id {
  font-size: 13px;
  color: #999;
}

.order-status {
  font-size: 12px;
  color: #e4393c;
  font-weight: 500;
}

.order-body {
  display: flex;
  gap: 40px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 15px;
  font-weight: 500;
}
</style>
