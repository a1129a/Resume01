import { createRouter, createWebHistory } from 'vue-router'

// 导入用户状态仓库
import { useUserStore } from '../store/user'

// 开发环境操作模式
const DEV_MODE = true  // 开发模式，可以绕过登录直接访问需要授权的页面
const DEV_USER = {
  id: 1,
  username: 'dev_user',
  nickname: '开发测试账号',
  avatarUrl: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  role: 'ROLE_USER'
}

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  // 注册功能已集成到登录页面，用户初次登录时自动注册
  // 删除独立的注册页面路由
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/resume/create',
    name: 'ResumeCreate',
    component: () => import('@/views/resume/Create.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/resume/list',
    name: 'ResumeList',
    component: () => import('@/views/resume/List.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 获取token和用户存储
  const token = localStorage.getItem('token')
  const userStore = useUserStore()
  
  // 开发模式处理
  if (DEV_MODE && to.meta.requiresAuth && !token) {
    console.log('开发模式: 绕过登录校验，自动使用开发账号')
    
    // 开发模式下，自动设置模拟的用户信息和token
    localStorage.setItem('token', 'dev_token_' + Date.now())
    userStore.setUserInfo(DEV_USER)
    
    // 允许访问受保护路由
    next()
    return
  }
  
  // 正常的登录校验
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
