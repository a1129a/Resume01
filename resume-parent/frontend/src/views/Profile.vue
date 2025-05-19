<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="profile-header">
          <h2>个人资料</h2>
          <el-button type="primary" @click="isEditing = true" v-if="!isEditing">编辑资料</el-button>
        </div>
      </template>
      
      <div class="profile-content">
        <el-form 
          :model="profileForm" 
          :rules="profileRules" 
          ref="profileFormRef"
          label-position="top"
          :disabled="!isEditing">
          
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="profileForm.username" placeholder="用户名" :disabled="true" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="电子邮箱" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="profileForm.name" placeholder="真实姓名" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="电话" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item v-if="isEditing">
            <div class="profile-actions">
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </div>
          </el-form-item>
        </el-form>
        
        <el-divider />
        
        <div class="change-password-section">
          <h3>修改密码</h3>
          <el-form 
            :model="passwordForm" 
            :rules="passwordRules" 
            ref="passwordFormRef"
            label-position="top">
            
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input 
                v-model="passwordForm.currentPassword" 
                type="password" 
                placeholder="请输入当前密码"
                show-password />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码"
                show-password />
            </el-form-item>
            
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码"
                show-password />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="changePassword">更新密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

export default {
  name: 'Profile',
  setup() {
    const userStore = useUserStore()
    const profileFormRef = ref(null)
    const passwordFormRef = ref(null)
    const isEditing = ref(false)
    
    // 个人资料表单
    const profileForm = reactive({
      username: '',
      email: '',
      name: '',
      phone: ''
    })
    
    // 修改密码表单
    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    // 表单验证规则
    const profileRules = {
      email: [
        { required: true, message: '请输入电子邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的电子邮箱地址', trigger: 'blur' }
      ],
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
      ]
    }
    
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else {
        if (passwordForm.confirmPassword !== '') {
          passwordFormRef.value.validateField('confirmPassword')
        }
        callback()
      }
    }
    
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    const passwordRules = {
      currentPassword: [
        { required: true, message: '请输入当前密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' },
        { validator: validatePass, trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { validator: validatePass2, trigger: 'blur' }
      ]
    }
    
    // 取消编辑
    const cancelEdit = () => {
      isEditing.value = false
      loadUserProfile() // 重新加载数据
    }
    
    // 保存个人资料
    const saveProfile = () => {
      profileFormRef.value.validate(async (valid) => {
        if (!valid) return
        
        try {
          // 此处添加更新个人资料的API调用
          ElMessage.success('个人资料已更新')
          isEditing.value = false
        } catch (error) {
          ElMessage.error('个人资料更新失败')
        }
      })
    }
    
    // 修改密码
    const changePassword = () => {
      passwordFormRef.value.validate(async (valid) => {
        if (!valid) return
        
        try {
          // 此处添加修改密码的API调用
          ElMessage.success('密码已更新')
          
          // 清空表单
          passwordForm.currentPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
        } catch (error) {
          ElMessage.error('密码更新失败')
        }
      })
    }
    
    // 加载用户资料
    const loadUserProfile = () => {
      const user = userStore.currentUser
      if (user) {
        profileForm.username = user.username || ''
        profileForm.email = user.email || ''
        profileForm.name = user.name || ''
        profileForm.phone = user.phone || ''
      }
    }
    
    onMounted(() => {
      loadUserProfile()
    })
    
    return {
      profileFormRef,
      passwordFormRef,
      profileForm,
      passwordForm,
      profileRules,
      passwordRules,
      isEditing,
      cancelEdit,
      saveProfile,
      changePassword
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.profile-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  padding: 10px 0;
}

.profile-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.change-password-section {
  margin-top: 20px;
}

.el-divider {
  margin: 30px 0;
}
</style>
