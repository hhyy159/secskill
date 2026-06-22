<template>
  <div class="login-page">
    <div class="login-card card">
      <h1 class="login-title">⚡ 秒杀系统</h1>
      <p class="login-subtitle">{{ isLogin ? '欢迎回来' : '创建新账号' }}</p>

      <!-- 提示消息 -->
      <div v-if="message" :class="['message', messageType === 'error' ? 'message-error' : 'message-success']">
        {{ message }}
      </div>

      <!-- 表单 -->
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>账号</label>
          <input v-model="account" type="text" placeholder="请输入账号" required autocomplete="username" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" required autocomplete="current-password" />
        </div>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '请稍候...' : (isLogin ? '登录' : '注册') }}
        </button>
      </form>

      <!-- 切换登录/注册 -->
      <p class="toggle-text">
        {{ isLogin ? '还没有账号？' : '已有账号？' }}
        <a href="#" @click.prevent="toggleMode">{{ isLogin ? '去注册' : '去登录' }}</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'

const router = useRouter()

const isLogin = ref(true)
const account = ref('')
const password = ref('')
const loading = ref(false)
const message = ref('')
const messageType = ref('error')

function toggleMode() {
  isLogin.value = !isLogin.value
  message.value = ''
  account.value = ''
  password.value = ''
}

async function handleSubmit() {
  if (!account.value || !password.value) {
    message.value = '请填写账号和密码'
    messageType.value = 'error'
    return
  }

  loading.value = true
  message.value = ''

  try {
    if (isLogin.value) {
      const res = await userApi.login(account.value, password.value)
      // Result格式: { code: 200, message: "success", data: "jwt..." }
      if (res.code === 200 && res.data) {
        localStorage.setItem('token', res.data)
        router.push('/products')
      } else {
        message.value = res.message || '登录失败'
        messageType.value = 'error'
      }
    } else {
      const res = await userApi.register(account.value, password.value)
      if (res.code === 200 && res.data) {
        localStorage.setItem('token', res.data)
        router.push('/products')
      } else {
        message.value = res.message || '注册失败'
        messageType.value = 'error'
      }
    }
  } catch (e) {
    message.value = e?.message || '网络错误，请稍后重试'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e4393c 0%, #ff6b6b 100%);
}

.login-card {
  width: 380px;
  padding: 40px;
}

.login-title {
  text-align: center;
  font-size: 28px;
  color: #333;
  margin-bottom: 4px;
}

.login-subtitle {
  text-align: center;
  color: #999;
  font-size: 14px;
  margin-bottom: 24px;
}

.toggle-text {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #999;
}

.toggle-text a {
  color: #e4393c;
  text-decoration: none;
}

.toggle-text a:hover {
  text-decoration: underline;
}
</style>
