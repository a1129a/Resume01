<template>
  <div class="dashboard-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="welcome-header">
          <h2>欢迎使用简历一键生成与投递助手</h2>
        </div>
      </template>
      <div class="dashboard-content">
        <h3>{{ greeting }}, {{ username }}</h3>
        <p>今天是 {{ currentDate }}，祝您求职顺利!</p>
        
        <div class="dashboard-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <h4>简历数量</h4>
                <div class="stat-value">{{ resumeCount }}</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <h4>投递次数</h4>
                <div class="stat-value">{{ deliveryCount }}</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="stat-card">
                <h4>面试邀请</h4>
                <div class="stat-value">{{ interviewCount }}</div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        
        <div class="quick-actions">
          <h3>快捷操作</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-button 
                type="primary" 
                icon="el-icon-plus" 
                class="action-button"
                @click="$router.push('/resume/create')">
                创建新简历
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button 
                type="success" 
                icon="el-icon-document" 
                class="action-button"
                @click="$router.push('/resume/list')">
                我的简历
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button 
                type="warning" 
                icon="el-icon-upload2" 
                class="action-button"
                @click="deliveryAction">
                一键投递
              </el-button>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'Dashboard',
  setup() {
    const userStore = useUserStore()
    const username = computed(() => userStore.userInfo?.username || '用户')
    const resumeCount = ref(0)
    const deliveryCount = ref(0)
    const interviewCount = ref(0)
    
    // 根据时间生成问候语
    const greeting = computed(() => {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      else if (hour < 9) return '早上好'
      else if (hour < 12) return '上午好'
      else if (hour < 14) return '中午好'
      else if (hour < 18) return '下午好'
      else if (hour < 22) return '晚上好'
      else return '夜深了'
    })
    
    // 生成当前日期
    const currentDate = computed(() => {
      const date = new Date()
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
    })
    
    // 快捷操作 - 一键投递
    const deliveryAction = () => {
      ElMessage.info('一键投递功能即将上线，敬请期待！')
    }
    
    onMounted(async () => {
      // 模拟加载数据（后续会从API获取）
      setTimeout(() => {
        resumeCount.value = 2
        deliveryCount.value = 5
        interviewCount.value = 1
      }, 1000)
    })
    
    return {
      username,
      greeting,
      currentDate,
      resumeCount,
      deliveryCount,
      interviewCount,
      deliveryAction
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-content {
  padding: 10px 0;
}

.dashboard-stats {
  margin: 30px 0;
}

.stat-card {
  text-align: center;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.quick-actions {
  margin-top: 30px;
}

.action-button {
  width: 100%;
  margin-bottom: 15px;
  padding: 15px 0;
}
</style>
