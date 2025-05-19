/**
 * 认证相关工具函数
 */

// 获取token
export function getToken() {
  return localStorage.getItem('token')
}

// 设置token
export function setToken(token) {
  return localStorage.setItem('token', token)
}

// 移除token
export function removeToken() {
  return localStorage.removeItem('token')
}

// 获取用户信息
export function getUserInfo() {
  const userString = localStorage.getItem('user')
  return userString ? JSON.parse(userString) : null
}

// 设置用户信息
export function setUserInfo(user) {
  return localStorage.setItem('user', JSON.stringify(user))
}

// 移除用户信息
export function removeUserInfo() {
  return localStorage.removeItem('user')
}

// 清除所有认证信息
export function clearAuth() {
  removeToken()
  removeUserInfo()
}
