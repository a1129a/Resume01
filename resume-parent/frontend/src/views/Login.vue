<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1 class="title">简历助手</h1>
        <p class="subtitle">快速生成专业简历</p>
      </div>

      <div class="login-content">
        <!-- 手机号登录 -->
        <div class="login-form">
          <el-form 
            :model="phoneForm" 
            :rules="phoneRules" 
            ref="phoneFormRef"
            @submit.prevent="handlePhoneLogin">
            
            <el-form-item prop="phone">
              <div class="input-prefix">
                <span class="country-code">+86</span>
              </div>
              <el-input 
                v-model="phoneForm.phone" 
                placeholder="请输入手机号"
                class="phone-input" />
            </el-form-item>
            
            <el-form-item prop="verifyCode">
              <div class="verification-code-container">
                <el-input 
                  v-model="phoneForm.verifyCode" 
                  placeholder="请输入验证码" />
                <el-button 
                  class="send-code-button" 
                  :disabled="countdown > 0 || !phoneForm.phone || !isValidPhone(phoneForm.phone)"
                  @click="sendSmsCode">
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                :loading="loading.phone" 
                class="login-button" 
                @click="handlePhoneLogin">
                登录 / 注册
              </el-button>
            </el-form-item>
          </el-form>

          <div class="agreement">
            <el-checkbox v-model="agreement">我已阅读并同意</el-checkbox>
            <a href="#" class="agreement-link">《用户协议》</a>
            <a href="#" class="agreement-link">《隐私政策》</a>
          </div>
        </div>

        <!-- 登录方式切换 -->
        <div class="login-methods">
          <p class="methods-title">其他登录方式</p>
          <div class="methods-icons">
            <div class="method-icon wechat-icon" @click="switchToWechat">
              <el-icon><ChatDotRound /></el-icon>
            </div>
          </div>
        </div>

        <!-- 微信登录 -->
        <div class="wechat-login" v-if="showWechat">
          <div class="wechat-header">
            <span class="back-to-phone" @click="showWechat = false">
              <el-icon><ArrowLeft /></el-icon> 返回手机登录
            </span>
          </div>
          
          <div class="qrcode-container" v-if="wechatQrUrl">
            <img :src="wechatQrUrl" alt="微信二维码" class="qrcode-image" />
            <p class="qrcode-tip">请使用微信扫一扫登录</p>
            <div class="qrcode-refresh" @click="refreshWechatQrCode">
              <el-icon><Refresh /></el-icon> 刷新二维码
            </div>
          </div>
          
          <div v-else class="qrcode-loading">
            <el-skeleton>
              <template #template>
                <el-skeleton-item variant="image" style="width: 200px; height: 200px; margin: 0 auto" />
                <div style="padding: 14px">
                  <el-skeleton-item variant="p" style="width: 100%" />
                </div>
              </template>
            </el-skeleton>
            <div class="get-qrcode" @click="getWechatQrCode">
              <el-icon><Refresh /></el-icon> 获取二维码
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, ArrowLeft, Refresh } from '@element-plus/icons-vue'
import { sendSmsCode as apiSendSmsCode, phoneLogin as apiPhoneLogin, getWechatQrCode as apiGetWechatQrCode, checkWechatLoginStatus } from '@/api/auth'
import { useUserStore } from '@/store/user'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const phoneFormRef = ref(null)
    const wechatQrUrl = ref('')
    const wechatState = ref('')
    const countdown = ref(0)
    const checkTimer = ref(null)
    const countdownTimer = ref(null)
    const showWechat = ref(false)
    const agreement = ref(true)
    
    // loading状态
    const loading = reactive({
      phone: false,
      wechat: false
    })
    
    // 手机号登录表单
    const phoneForm = reactive({
      phone: '',
      verifyCode: ''
    })
    
    // 手机号登录验证规则
    const phoneRules = {
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      verifyCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
      ]
    }
    
    // 验证手机号格式
    const isValidPhone = (phone) => {
      return /^1[3-9]\d{9}$/.test(phone)
    }
    
    // 切换到微信登录
    const switchToWechat = () => {
      showWechat.value = true
      getWechatQrCode()
    }
    
    // 处理登录成功通用方法
    const handleLoginSuccess = (response) => {
      // 保存token
      localStorage.setItem('token', response.data.token)
      
      // 更新用户状态
      userStore.setUserInfo(response.data.user)
      
      ElMessage({
        message: '登录成功',
        type: 'success',
        duration: 1500
      })
      
      // 跳转到主页
      setTimeout(() => {
        router.push('/dashboard')
      }, 1000)
    }
    
    // 发送手机验证码
    const sendSmsCode = async () => {
      // 验证手机号
      if (!isValidPhone(phoneForm.phone)) {
        ElMessage.warning('请输入正确的手机号')
        return
      }
      
      // 验证协议是否同意
      if (!agreement.value) {
        ElMessage.warning('请阅读并同意用户协议和隐私政策')
        return
      }
      
      try {
        const response = await apiSendSmsCode({ phone: phoneForm.phone })
        if (response.code === 200) {
          ElMessage({
            message: '验证码已发送，请注意查收',
            type: 'success'
          })
          
          // 开始倒计时
          countdown.value = 60
          countdownTimer.value = setInterval(() => {
            countdown.value--
            if (countdown.value <= 0) {
              clearInterval(countdownTimer.value)
            }
          }, 1000)
        }
      } catch (error) {
        console.error('发送验证码失败:', error)
        ElMessage.error('发送验证码失败，请稍后重试')
      }
    }
    
    // 手机号验证码登录/注册
    const handlePhoneLogin = () => {
      // 验证协议是否同意
      if (!agreement.value) {
        ElMessage.warning('请阅读并同意用户协议和隐私政策')
        return
      }
      
      phoneFormRef.value.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        loading.phone = true
        try {
          const response = await apiPhoneLogin(phoneForm)
          if (response.code === 200) {
            handleLoginSuccess(response)
          }
        } catch (error) {
          console.error('手机登录失败:', error)
          ElMessage.error('验证码错误或已过期')
        } finally {
          loading.phone = false
        }
      })
    }
    
    // 获取微信扫码登录二维码
    const getWechatQrCode = async () => {
      loading.wechat = true
      try {
        const response = await apiGetWechatQrCode()
        if (response.code === 200) {
          wechatQrUrl.value = response.data.qrCodeUrl
          wechatState.value = response.data.state
          
          // 开始轮询检查扫码状态
          startCheckLoginStatus()
        }
      } catch (error) {
        console.error('获取微信二维码失败:', error)
        ElMessage.error('获取微信二维码失败，请重试')
      } finally {
        loading.wechat = false
      }
    }
    
    // 刷新微信二维码
    const refreshWechatQrCode = () => {
      // 清除旧的二维码和状态检查
      wechatQrUrl.value = ''
      if (checkTimer.value) {
        clearInterval(checkTimer.value)
      }
      
      // 重新获取二维码
      getWechatQrCode()
    }
    
    // 开始轮询检查微信登录状态
    const startCheckLoginStatus = () => {
      // 清除旧的定时器
      if (checkTimer.value) {
        clearInterval(checkTimer.value)
      }
      
      // 每3秒检查一次登录状态
      checkTimer.value = setInterval(async () => {
        try {
          const response = await checkWechatLoginStatus(wechatState.value)
          if (response.code === 200 && response.data) {
            // 扫码登录成功
            clearInterval(checkTimer.value)
            handleLoginSuccess(response.data)
          }
        } catch (error) {
          console.error('检查微信登录状态失败:', error)
        }
      }, 3000)
    }
    
    // 组件挂载时初始化
    onMounted(() => {
      // 如果当前是微信登录，自动获取二维码
      if (showWechat.value) {
        getWechatQrCode()
      }
    })
    
    // 组件卸载前清除定时器
    onBeforeUnmount(() => {
      if (checkTimer.value) {
        clearInterval(checkTimer.value)
      }
      if (countdownTimer.value) {
        clearInterval(countdownTimer.value)
      }
    })
    
    return {
      phoneFormRef,
      phoneForm,
      phoneRules,
      loading,
      wechatQrUrl,
      countdown,
      showWechat,
      agreement,
      isValidPhone,
      switchToWechat,
      sendSmsCode,
      handlePhoneLogin,
      getWechatQrCode,
      refreshWechatQrCode
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f7f8fa;
  padding: 20px;
}

.login-box {
  width: 400px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 40px 30px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 15px;
  color: #8a8a8a;
  margin: 0;
}

.login-content {
  position: relative;
}

.login-form {
  margin-bottom: 25px;
}

.input-prefix {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 15px;
  color: #333;
  font-weight: 500;
  border-right: 1px solid #e4e7ed;
  z-index: 1;
}

.country-code {
  margin-right: 10px;
}

.phone-input :deep(.el-input__inner) {
  padding-left: 60px !important;
}

:deep(.el-input__inner) {
  height: 50px;
  border-radius: 4px;
}

.verification-code-container {
  display: flex;
  align-items: center;
}

.verification-code-container :deep(.el-input) {
  flex: 1;
}

.send-code-button {
  width: 120px;
  margin-left: 10px;
  height: 50px;
  border: 1px solid #eee;
  border-radius: 4px;
  font-size: 14px;
  background-color: #fff;
  color: #427BFF;
}

.send-code-button:hover:not(:disabled) {
  background-color: #f5f7fa;
  border-color: #427BFF;
}

.send-code-button:disabled {
  color: #bbb;
  background-color: #f7f7f7;
  border-color: #e4e7ed;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  margin-top: 20px;
  background-color: #427BFF;
  border-color: #427BFF;
}

.login-button:hover {
  background-color: #3b6fe5;
  border-color: #3b6fe5;
}

.agreement {
  margin-top: 15px;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.agreement-link {
  color: #427BFF;
  text-decoration: none;
  margin: 0 2px;
}

.login-methods {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.methods-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

.methods-icons {
  display: flex;
  justify-content: center;
}

.method-icon {
  width: 44px;
  height: 44px;
  background-color: #f7f7f7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-icon:hover {
  background-color: #e6e6e6;
}

.wechat-icon {
  color: #2EB52F;
  font-size: 28px;
}

.wechat-login {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
  z-index: 10;
}

.wechat-header {
  margin-bottom: 30px;
}

.back-to-phone {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.back-to-phone i {
  margin-right: 5px;
}

.back-to-phone:hover {
  color: #427BFF;
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.qrcode-image {
  width: 200px;
  height: 200px;
  margin-bottom: 15px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.qrcode-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.qrcode-refresh, .get-qrcode {
  font-size: 14px;
  color: #427BFF;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-refresh i, .get-qrcode i {
  margin-right: 5px;
}

.qrcode-loading {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 修复图标样式 */
:deep(.el-icon) {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 添加微信图标样式 */
.wechat-icon i {
  font-size: 24px;
  color: #2EB52F;
}

@media screen and (max-width: 480px) {
  .login-box {
    width: 100%;
    padding: 30px 20px;
    box-shadow: none;
  }
  
  .verification-code-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .send-code-button {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
  }
}
</style>
