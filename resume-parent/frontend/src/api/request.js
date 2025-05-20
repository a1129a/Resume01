import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.params || config.data)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log('响应数据:', response.config.url, res)
    
    // 判断是否是成功的请求
    if (res.code && res.code !== 200) {
      console.error('响应错误:', response.config.url, res)
      ElMessage.error(res.message || '请求失败')
      
      // 处理特定错误码
      if (res.code === 401) {
        // Token失效，清除本地token并重定向到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    
    // 处理特定状态码
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default service
