<template>
  <div class="container">
    <h2 style="margin: 20px 0; font-size: 22px;">秒杀商品</h2>

    <!-- 加载中 -->
    <div v-if="loading" style="text-align: center; padding: 60px 0; color: #999;">
      加载中...
    </div>

    <!-- 空列表 -->
    <div v-else-if="products.length === 0" style="text-align: center; padding: 60px 0; color: #999;">
      暂无秒杀商品
    </div>

    <!-- 商品列表 -->
    <div v-for="product in products" :key="product.product_id" class="card product-card">
      <div class="product-header">
        <h3 class="product-name">{{ product.product_name }}</h3>
        <span :class="['badge', statusClass(product.status)]">{{ product.status }}</span>
      </div>
      <p class="product-desc">{{ product.description }}</p>
      <div class="product-footer">
        <div class="product-info">
          <span>库存：<strong>{{ product.stock }}</strong></span>
          <span>
            {{ formatTime(product.start_time) }} — {{ formatTime(product.end_time) }}
          </span>
        </div>
        <router-link
          :to="'/product/' + product.product_id"
          class="btn btn-primary"
          :class="{ disabled: product.status !== '进行中' }"
        >
          {{ product.status === '进行中' ? '立即秒杀' : '查看详情' }}
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi } from '../api'

const products = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await productApi.list()
    if (res.code === 200) {
      products.value = res.data || []
    }
  } catch (e) {
    console.error('获取商品列表失败', e)
  } finally {
    loading.value = false
  }
})

function statusClass(status) {
  const map = {
    '未开始': 'badge-upcoming',
    '进行中': 'badge-active',
    '已售罄': 'badge-soldout',
    '已结束': 'badge-ended'
  }
  return map[status] || ''
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  // 简单截取，去掉秒和T
  return timeStr.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.product-card {
  padding: 20px 24px;
}

.product-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
}

.product-desc {
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.product-info strong {
  color: #e4393c;
}

.btn.disabled {
  background: #ccc;
  pointer-events: none;
}
</style>
