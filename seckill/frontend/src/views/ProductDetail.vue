<template>
  <div class="container">
    <!-- 返回按钮 -->
    <router-link to="/products" class="back-link">← 返回商品列表</router-link>

    <!-- 加载中 -->
    <div v-if="loading" style="text-align: center; padding: 60px 0; color: #999;">
      加载中...
    </div>

    <!-- 商品详情 -->
    <div v-else-if="product" class="card">
      <div class="detail-header">
        <h2>{{ product.product_name }}</h2>
        <span :class="['badge', statusClass(product.status)]">{{ product.status }}</span>
      </div>

      <p class="detail-desc">{{ product.description }}</p>

      <div class="detail-meta">
        <div class="meta-item">
          <span class="meta-label">剩余库存</span>
          <span class="meta-value stock">{{ product.stock }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">开始时间</span>
          <span class="meta-value">{{ formatTime(product.start_time) }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">结束时间</span>
          <span class="meta-value">{{ formatTime(product.end_time) }}</span>
        </div>
      </div>

      <!-- 提示消息 -->
      <div v-if="message" :class="['message', messageType === 'error' ? 'message-error' : 'message-success']">
        {{ message }}
      </div>

      <!-- 秒杀按钮 -->
      <button
        class="btn btn-primary btn-block seckill-btn"
        :disabled="product.status !== '进行中' || seckilling"
        @click="handleSeckill"
      >
        {{ seckillBtnText }}
      </button>
    </div>

    <!-- 不存在 -->
    <div v-else style="text-align: center; padding: 60px 0; color: #999;">
      商品不存在
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, seckillApi } from '../api'

const route = useRoute()
const productId = route.params.id

const product = ref(null)
const loading = ref(true)
const seckilling = ref(false)
const message = ref('')
const messageType = ref('error')

const seckillBtnText = computed(() => {
  if (seckilling.value) return '秒杀中...'
  if (!product.value) return '加载中...'
  const map = {
    '未开始': '秒杀尚未开始',
    '进行中': '立即秒杀',
    '已售罄': '已售罄',
    '已结束': '秒杀已结束'
  }
  return map[product.value.status] || '无法秒杀'
})

onMounted(async () => {
  try {
    const res = await productApi.detail(productId)
    if (res.code === 200) {
      product.value = res.data
    }
  } catch (e) {
    console.error('获取商品详情失败', e)
  } finally {
    loading.value = false
  }
})

async function handleSeckill() {
  seckilling.value = true
  message.value = ''

  try {
    const res = await seckillApi.execute(productId)
    if (res.code === 200) {
      message.value = res.message || '秒杀成功，订单处理中！'
      messageType.value = 'success'
    } else {
      message.value = res.message || '秒杀失败'
      messageType.value = 'error'
    }
  } catch (e) {
    message.value = e?.message || '网络错误，请稍后重试'
    messageType.value = 'error'
  } finally {
    seckilling.value = false
  }
}

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
  return timeStr.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.back-link {
  display: inline-block;
  margin: 16px 0;
  color: #e4393c;
  text-decoration: none;
  font-size: 14px;
}

.back-link:hover {
  text-decoration: underline;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.detail-header h2 {
  font-size: 24px;
}

.detail-desc {
  color: #666;
  line-height: 1.8;
  margin-bottom: 24px;
  font-size: 15px;
}

.detail-meta {
  display: flex;
  gap: 40px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #999;
}

.meta-value {
  font-size: 16px;
  font-weight: 500;
}

.meta-value.stock {
  color: #e4393c;
  font-weight: bold;
  font-size: 22px;
}

.seckill-btn {
  padding: 14px 0;
  font-size: 16px;
  letter-spacing: 2px;
}
</style>
