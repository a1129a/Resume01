import { defineStore } from 'pinia'
import { login, register, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || null,
    loading: false,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user
  },
  
  actions: {
    async loginUser(credentials) {
      this.loading = true
      this.error = null
      try {
        const response = await login(credentials)
        if (response.code === 200) {
          const { token, user } = response.data
          this.setUserData(token, user)
          return true
        } else {
          this.error = response.message || '登录失败'
          return false
        }
      } catch (error) {
        this.error = error.message || '登录时发生错误'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async registerUser(userData) {
      this.loading = true
      this.error = null
      try {
        const response = await register(userData)
        if (response.code === 200) {
          const { token, user } = response.data
          this.setUserData(token, user)
          return true
        } else {
          this.error = response.message || '注册失败'
          return false
        }
      } catch (error) {
        this.error = error.message || '注册时发生错误'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchUserInfo() {
      if (!this.token) return
      
      this.loading = true
      try {
        const response = await getUserInfo()
        if (response.code === 200) {
          this.user = response.data
          localStorage.setItem('user', JSON.stringify(response.data))
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        if (error.response?.status === 401) {
          this.logout()
        }
      } finally {
        this.loading = false
      }
    },
    
    setUserData(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },

    // 开发模式下设置用户信息
    setUserInfo(user) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
  }
})
