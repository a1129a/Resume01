<template>
  <div class="resume-list-container">
    <!-- 顶部导航栏 -->
    <div class="resume-header">
      <div class="resume-header-left">
        <i class="el-icon-arrow-left back-icon" @click="goBack"></i>
        <h1 class="header-title">我的简历</h1>
      </div>
      <div class="resume-header-right">
        <el-button type="primary" class="new-resume-btn" @click="$router.push('/resume/create')">
          <i class="el-icon-plus"></i> 创建新简历
        </el-button>
      </div>
    </div>
    
    <!-- 简历列表内容 -->
    <div class="resume-content">
      <!-- 无简历时的提示 -->
      <div v-if="resumeList.length === 0" class="empty-resume-container">
        <div class="empty-resume-content">
          <img src="https://img.bosszhipin.com/static/file/2020/r3b7ljtk81617694476458.png" alt="空简历" class="empty-img">
          <h3>您还没有创建简历</h3>
          <p class="empty-desc">一份专业的简历能帮助您更快找到心仪的工作</p>
          <el-button type="primary" size="large" class="create-resume-btn" @click="$router.push('/resume/create')">
            <i class="el-icon-plus"></i> 创建简历
          </el-button>
        </div>
      </div>
      
      <!-- 有简历时的列表展示 -->
      <div v-else class="resume-list-wrapper">
        <!-- 筛选和排序工具栏 -->
        <div class="tool-bar">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="card">卡片视图</el-radio-button>
            <el-radio-button label="list">列表视图</el-radio-button>
          </el-radio-group>
          
          <div class="sort-actions">
            <span class="sort-label">排序方式：</span>
            <el-select v-model="sortBy" size="small" style="width: 120px">
              <el-option label="最近更新" value="updateTime" />
              <el-option label="创建时间" value="createTime" />
              <el-option label="名称排序" value="title" />
            </el-select>
          </div>
        </div>
        
        <!-- 卡片式视图 -->
        <div v-if="viewMode === 'card'" class="card-view">
          <el-row :gutter="24">
            <el-col v-for="resume in sortedResumeList" :key="resume.id" :span="8">
              <div class="resume-card">
                <div class="resume-card-header">
                  <h3 class="resume-title">{{ resume.title }}</h3>
                  <el-tag size="small" type="primary" effect="plain" class="position-tag">{{ resume.position }}</el-tag>
                </div>
                
                <div class="resume-card-body">
                  <div class="resume-preview">
                    <img :src="getResumePreviewImage(resume)" alt="简历预览" class="preview-img">
                  </div>
                  
                  <div class="resume-meta">
                    <p class="update-time">
                      <i class="el-icon-time"></i> 最近更新：{{ formatDateRelative(resume.updateTime) }}
                    </p>
                    <p class="completeness">
                      <span>完整度：</span>
                      <el-progress :percentage="getCompletenessPercentage(resume)" :format="percentageFormat" />
                    </p>
                  </div>
                </div>
                
                <div class="resume-card-footer">
                  <div class="resume-stats">
                    <div class="stat-item">
                      <i class="el-icon-view"></i>
                      <span>{{ resume.viewCount || 0 }}</span>
                    </div>
                    <div class="stat-item">
                      <i class="el-icon-download"></i>
                      <span>{{ resume.downloadCount || 0 }}</span>
                    </div>
                    <div class="stat-item">
                      <i class="el-icon-message"></i>
                      <span>{{ resume.deliveryCount || 0 }}</span>
                    </div>
                  </div>
                  
                  <div class="resume-actions">
                    <el-button type="primary" size="small" @click="editResume(resume.id)" class="action-btn">
                      <i class="el-icon-edit"></i> 编辑
                    </el-button>
                    <el-dropdown trigger="click" @command="handleCommand($event, resume.id)">
                      <el-button size="small" class="action-btn">
                        <i class="el-icon-more"></i>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="preview">
                            <i class="el-icon-view"></i> 预览
                          </el-dropdown-item>
                          <el-dropdown-item command="download">
                            <i class="el-icon-download"></i> 下载
                          </el-dropdown-item>
                          <el-dropdown-item command="deliver">
                            <i class="el-icon-position"></i> 投递
                          </el-dropdown-item>
                          <el-dropdown-item command="share">
                            <i class="el-icon-share"></i> 分享
                          </el-dropdown-item>
                          <el-dropdown-item command="duplicate">
                            <i class="el-icon-document-copy"></i> 复制
                          </el-dropdown-item>
                          <el-dropdown-item command="delete" divided>
                            <i class="el-icon-delete"></i> 删除
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <!-- 列表式视图 -->
        <div v-else class="list-view">
          <el-table :data="sortedResumeList" style="width: 100%" row-class-name="resume-row">
            <el-table-column label="简历名称" min-width="200">
              <template #default="{row}">
                <div class="resume-title-cell">
                  <div class="resume-mini-preview">
                    <img :src="getResumePreviewImage(row)" alt="简历预览" class="mini-preview-img">
                  </div>
                  <div class="resume-title-info">
                    <h4>{{ row.title }}</h4>
                    <el-tag size="small" type="info" effect="plain">{{ row.position }}</el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="完整度" width="150">
              <template #default="{row}">
                <el-progress :percentage="getCompletenessPercentage(row)" :format="percentageFormat" />
              </template>
            </el-table-column>
            
            <el-table-column label="简历数据" width="180">
              <template #default="{row}">
                <div class="resume-stats-cell">
                  <div class="stat-item">
                    <i class="el-icon-view"></i>
                    <span>{{ row.viewCount || 0 }} 浏览</span>
                  </div>
                  <div class="stat-item">
                    <i class="el-icon-download"></i>
                    <span>{{ row.downloadCount || 0 }} 下载</span>
                  </div>
                  <div class="stat-item">
                    <i class="el-icon-message"></i>
                    <span>{{ row.deliveryCount || 0 }} 投递</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="更新时间" width="180">
              <template #default="{row}">
                <div class="time-info">
                  <p><i class="el-icon-time"></i> {{ formatDate(row.updateTime) }}</p>
                  <p class="create-time">创建于 {{ formatDate(row.createTime) }}</p>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" fixed="right" width="160">
              <template #default="{row}">
                <div class="table-actions">
                  <el-button type="primary" size="small" @click="editResume(row.id)">编辑</el-button>
                  <el-dropdown @command="handleCommand($event, row.id)">
                    <el-button size="small">
                      <i class="el-icon-more"></i>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="preview">预览</el-dropdown-item>
                        <el-dropdown-item command="download">下载</el-dropdown-item>
                        <el-dropdown-item command="deliver">投递</el-dropdown-item>
                        <el-dropdown-item command="duplicate">复制</el-dropdown-item>
                        <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ResumeList',
  setup() {
    const router = useRouter()
    const viewMode = ref('card')
    const sortBy = ref('updateTime')
    
    // 模拟数据 - 实际项目中应改为API调用
    const resumeList = ref([
      {
        id: 1,
        title: '前端开发工程师简历',
        position: '前端开发工程师',
        createTime: '2025-05-19T10:00:00',
        updateTime: '2025-05-19T10:30:00',
        template: 'modern',
        completeness: 80,
        viewCount: 32,
        downloadCount: 5,
        deliveryCount: 8
      },
      {
        id: 2,
        title: '后端开发工程师简历',
        position: '后端开发工程师',
        createTime: '2025-05-18T14:00:00',
        updateTime: '2025-05-19T09:15:00',
        template: 'professional',
        completeness: 65,
        viewCount: 15,
        downloadCount: 2,
        deliveryCount: 3
      },
      {
        id: 3,
        title: '产品经理简历',
        position: '产品经理',
        createTime: '2025-05-17T09:00:00',
        updateTime: '2025-05-19T08:45:00',
        template: 'creative',
        completeness: 92,
        viewCount: 56,
        downloadCount: 10,
        deliveryCount: 12
      }
    ])
    
    // 返回上一页
    const goBack = () => {
      router.push('/dashboard')
    }
    
    // 排序后的简历列表
    const sortedResumeList = computed(() => {
      const clonedList = [...resumeList.value]
      
      if (sortBy.value === 'updateTime') {
        return clonedList.sort((a, b) => new Date(b.updateTime) - new Date(a.updateTime))
      } else if (sortBy.value === 'createTime') {
        return clonedList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      } else if (sortBy.value === 'title') {
        return clonedList.sort((a, b) => a.title.localeCompare(b.title))
      }
      
      return clonedList
    })
    
    // 获取简历预览图片
    const getResumePreviewImage = (resume) => {
      const templates = {
        modern: 'https://img.bosszhipin.com/static/file/2020/eshzdbjr41617694397037.png',
        professional: 'https://img.bosszhipin.com/static/file/2020/4uewzhew41617694430819.png',
        creative: 'https://img.bosszhipin.com/static/file/2020/zdvvgfsp41617694464483.png',
        default: 'https://img.bosszhipin.com/static/file/2020/r3b7ljtk81617694476458.png'
      }
      
      return templates[resume.template] || templates.default
    }
    
    // 获取简历完整度百分比
    const getCompletenessPercentage = (resume) => {
      return resume.completeness || Math.floor(Math.random() * 60) + 40 // 模拟数据，实际项目中应计算完成度
    }
    
    // 格式化完整度百分比
    const percentageFormat = (percentage) => {
      return percentage + '%'
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
    
    // 相对日期格式化（例如："3天前"）
    const formatDateRelative = (dateString) => {
      const now = new Date()
      const date = new Date(dateString)
      const diffMs = now - date
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
      const diffMinutes = Math.floor(diffMs / (1000 * 60))
      
      if (diffDays > 30) {
        return formatDate(dateString)
      } else if (diffDays > 0) {
        return `${diffDays}天前`
      } else if (diffHours > 0) {
        return `${diffHours}小时前`
      } else if (diffMinutes > 0) {
        return `${diffMinutes}分钟前`
      } else {
        return '刚刚'
      }
    }
    
    // 处理下拉菜单命令
    const handleCommand = (command, id) => {
      switch (command) {
        case 'preview':
          previewResume(id)
          break
        case 'download':
          downloadResume(id)
          break
        case 'deliver':
          deliverResume(id)
          break
        case 'share':
          shareResume(id)
          break
        case 'duplicate':
          duplicateResume(id)
          break
        case 'delete':
          deleteResume(id)
          break
      }
    }
    
    // 编辑简历
    const editResume = (id) => {
      router.push(`/resume/create?id=${id}`)
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
    
    // 分享简历
    const shareResume = (id) => {
      ElMessage.info(`简历分享功能正在开发中，简历ID: ${id}`)
    }
    
    // 复制简历
    const duplicateResume = (id) => {
      ElMessage.info(`复制简历功能正在开发中，简历ID: ${id}`)
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
      viewMode,
      sortBy,
      sortedResumeList,
      goBack,
      formatDate,
      formatDateRelative,
      getResumePreviewImage,
      getCompletenessPercentage,
      percentageFormat,
      handleCommand,
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
/* 全局容器样式 */
.resume-list-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏样式 */
.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 24px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.resume-header-left {
  display: flex;
  align-items: center;
}

.back-icon {
  font-size: 20px;
  margin-right: 12px;
  cursor: pointer;
  color: #666;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.new-resume-btn {
  font-weight: 500;
}

/* 内容区域样式 */
.resume-content {
  flex: 1;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* 空简历状态 */
.empty-resume-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.empty-resume-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px 20px;
}

.empty-img {
  width: 160px;
  height: 160px;
  margin-bottom: 20px;
}

.empty-desc {
  color: #999;
  margin-bottom: 24px;
}

.create-resume-btn {
  min-width: 160px;
}

/* 工具栏样式 */
.resume-list-wrapper {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.tool-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}

.sort-actions {
  display: flex;
  align-items: center;
}

.sort-label {
  margin-right: 8px;
  color: #606266;
  font-size: 14px;
}

/* 卡片视图样式 */
.card-view {
  padding: 24px;
}

.resume-card {
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 24px;
  overflow: hidden;
}

.resume-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.resume-card-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.resume-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.position-tag {
  margin-top: 4px;
}

.resume-card-body {
  padding: 16px;
  flex: 1;
}

.resume-preview {
  width: 100%;
  height: 200px;
  margin-bottom: 16px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f9f9f9;
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.resume-meta {
  margin-top: 12px;
}

.update-time {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.completeness {
  margin-top: 8px;
}

.resume-card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.resume-stats {
  display: flex;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-right: 16px;
  font-size: 13px;
  color: #606266;
}

.stat-item i {
  margin-right: 4px;
  font-size: 14px;
}

.resume-actions {
  display: flex;
  justify-content: space-between;
}

.action-btn {
  flex: 1;
}

.action-btn + .action-btn {
  margin-left: 8px;
}

/* 列表视图样式 */
.list-view {
  padding: 0;
}

.resume-row {
  cursor: pointer;
  transition: background-color 0.3s;
}

.resume-row:hover {
  background-color: #f5f7fa;
}

.resume-title-cell {
  display: flex;
  align-items: center;
}

.resume-mini-preview {
  width: 56px;
  height: 56px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
  background-color: #f9f9f9;
}

.mini-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.resume-title-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
}

.resume-stats-cell {
  display: flex;
  flex-direction: column;
}

.resume-stats-cell .stat-item {
  margin-bottom: 4px;
}

.time-info {
  font-size: 13px;
}

.time-info p {
  margin: 0 0 4px 0;
}

.create-time {
  color: #909399;
  font-size: 12px;
}

.table-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.table-actions .el-button + .el-button {
  margin-left: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-view .el-col {
    width: 100%;
  }
  
  .resume-preview {
    height: 160px;
  }
  
  .tool-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .sort-actions {
    margin-top: 12px;
    width: 100%;
  }
}


</style>
