import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动携带 JWT Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器：统一错误处理
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(error.response.data || error)
    }
    return Promise.reject(error)
  }
)

// ========== 用户接口 ==========
export const userApi = {
  login(account, password) {
    return api.post('/login', null, { params: { account, password } })
  },
  register(account, password) {
    return api.post('/register', null, { params: { account, password } })
  }
}

// ========== 商品接口 ==========
export const productApi = {
  list() {
    return api.get('/product/list')
  },
  detail(productId) {
    return api.get('/product/detail', { params: { product_id: productId } })
  }
}

// ========== 秒杀接口 ==========
export const seckillApi = {
  execute(productId) {
    return api.post('/seckill', null, { params: { product_id: productId } })
  }
}

// ========== 订单接口 ==========
export const orderApi = {
  myOrders() {
    return api.get('/order/my')
  }
}

export default api
