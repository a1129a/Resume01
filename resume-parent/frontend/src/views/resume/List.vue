<template>
  <div class="resume-list-container">
    <div class="page-header">
      <h2>我的简历</h2>
      <el-button type="primary" @click="$router.push('/resume/create')">创建新简历</el-button>
    </div>
    
    <div class="resume-list">
      <el-empty v-if="resumeList.length === 0" description="暂无简历，开始创建吧">
        <el-button type="primary" @click="$router.push('/resume/create')">立即创建</el-button>
      </el-empty>
      
      <el-row v-else :gutter="20">
        <el-col v-for="(resume, index) in resumeList" :key="resume.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="resume-card" shadow="hover">
            <template #header>
              <div class="resume-card-header">
                <span>{{ resume.title }}</span>
                <el-dropdown>
                  <span class="el-dropdown-link">
                    <el-icon><more /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="editResume(resume.id)">编辑</el-dropdown-item>
                      <el-dropdown-item @click="previewResume(resume.id)">预览</el-dropdown-item>
                      <el-dropdown-item @click="downloadResume(resume.id)">下载</el-dropdown-item>
                      <el-dropdown-item @click="deliverResume(resume.id)">投递</el-dropdown-item>
                      <el-dropdown-item divided @click="deleteResume(resume.id)">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
            
            <div class="resume-card-content">
              <div class="resume-info">
                <p><strong>{{ resume.position }}</strong></p>
                <p>创建时间: {{ formatDate(resume.createTime) }}</p>
                <p>更新时间: {{ formatDate(resume.updateTime) }}</p>
              </div>
              <div class="resume-actions">
                <el-button type="primary" size="small" @click="editResume(resume.id)">编辑</el-button>
                <el-button type="success" size="small" @click="deliverResume(resume.id)">投递</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ResumeList',
  setup() {
    const resumeList = ref([
      {
        id: 1,
        title: '前端开发工程师简历',
        position: '前端开发工程师',
        createTime: '2025-05-19T10:00:00',
        updateTime: '2025-05-19T10:30:00'
      },
      {
        id: 2,
        title: '后端开发工程师简历',
        position: '后端开发工程师',
        createTime: '2025-05-18T14:00:00',
        updateTime: '2025-05-19T09:15:00'
      }
    ])
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
    
    // 编辑简历
    const editResume = (id) => {
      ElMessage.info(`编辑简历功能正在开发中，简历ID: ${id}`)
    }
    
    // 预览简历
    const previewResume = (id) => {
      ElMessage.info(`预览简历功能正在开发中，简历ID: ${id}`)
    }
    
    // 下载简历
    const downloadResume = (id) => {
      ElMessage.info(`下载简历功能正在开发中，简历ID: ${id}`)
    }
    
    // 投递简历
    const deliverResume = (id) => {
      ElMessage.info(`简历投递功能正在开发中，简历ID: ${id}`)
    }
    
    // 删除简历
    const deleteResume = (id) => {
      ElMessageBox.confirm(
        '确定要删除此简历吗？此操作不可撤销',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 此处添加删除API调用
        resumeList.value = resumeList.value.filter(resume => resume.id !== id)
        ElMessage.success('简历已删除')
      }).catch(() => {
        // 取消删除
      })
    }
    
    onMounted(() => {
      // 此处添加获取简历列表的API调用
    })
    
    return {
      resumeList,
      formatDate,
      editResume,
      previewResume,
      downloadResume,
      deliverResume,
      deleteResume
    }
  }
}
</script>

<style scoped>
.resume-list-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.resume-list {
  margin-top: 20px;
}

.resume-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.resume-card:hover {
  transform: translateY(-5px);
}

.resume-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume-card-content {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.resume-info {
  margin-bottom: 15px;
}

.resume-info p {
  margin: 5px 0;
  color: #606266;
}

.resume-actions {
  display: flex;
  justify-content: space-between;
}
</style>
