import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/v1/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/v1/users/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/v1/users/update',
    method: 'put',
    data
  })
}

// 微信扫码登录获取二维码
export function getWechatQrCode() {
  return request({
    url: '/v1/auth/wechat/qrcode',
    method: 'get'
  })
}

// 微信扫码登录状态检查
export function checkWechatLoginStatus(uuid) {
  return request({
    url: `/v1/auth/wechat/check/${uuid}`,
    method: 'get'
  })
}

// 发送短信验证码
export function sendSmsCode(data) {
  return request({
    url: '/v1/auth/sms/code',
    method: 'post',
    data
  })
}

// 手机号验证码登录
export function phoneLogin(data) {
  return request({
    url: '/v1/auth/phone/login',
    method: 'post',
    data
  })
}
