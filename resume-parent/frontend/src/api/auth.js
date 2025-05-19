import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/user-service/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/user-service/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user-service/users/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user-service/users/update',
    method: 'put',
    data
  })
}

// 微信扫码登录获取二维码
export function getWechatQrCode() {
  return request({
    url: '/user-service/auth/wechat/qrcode',
    method: 'get'
  })
}

// 微信扫码登录状态检查
export function checkWechatLoginStatus(uuid) {
  return request({
    url: `/user-service/auth/wechat/check/${uuid}`,
    method: 'get'
  })
}
