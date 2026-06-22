<template>
  <div id="app">
    <nav v-if="$route.path !== '/login'" class="navbar">
      <router-link to="/products" class="logo">⚡秒杀</router-link>
      <div class="nav-links">
        <router-link to="/products">商品列表</router-link>
        <router-link to="/orders">我的订单</router-link>
      </div>
      <button class="logout-btn" @click="logout">退出</button>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function logout() {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>
