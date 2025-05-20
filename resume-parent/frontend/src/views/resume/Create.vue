<template>
  <div class="resume-create-container">
    <!-- 顶部导航栏 -->
    <div class="resume-header">
      <div class="resume-header-left">
        <i class="el-icon-arrow-left back-icon" @click="goBack"></i>
        <h1 class="header-title">我的简历</h1>
      </div>
      <div class="resume-header-right">
        <el-button type="text" class="preview-btn" @click="previewResume">
          <i class="el-icon-view"></i> 预览
        </el-button>
        <el-button type="primary" class="save-btn" @click="saveResume">
          保存简历
        </el-button>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="resume-content">
      <!-- 左侧简历预览 -->
      <div class="resume-preview-panel">
        <div class="preview-card">
          <div class="preview-header">
            <div class="preview-avatar">
              <img :src="avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="头像" />
              <div class="avatar-upload">
                <i class="el-icon-camera"></i>
                <input type="file" @change="handleAvatarUpload" accept="image/*" class="avatar-input" />
              </div>
            </div>
            <div class="preview-basic-info">
              <h2>{{ basicInfo.name || '姓名' }}</h2>
              <p>{{ basicInfo.jobTitle || '期望职位' }}</p>
              <div class="contact-info">
                <span v-if="basicInfo.phone"><i class="el-icon-mobile-phone"></i> {{ basicInfo.phone }}</span>
                <span v-if="basicInfo.email"><i class="el-icon-message"></i> {{ basicInfo.email }}</span>
              </div>
            </div>
          </div>
          <div class="preview-sections">
            <div class="preview-section">
              <h3 class="section-title">个人概述</h3>
              <p>{{ basicInfo.summary || '请在右侧填写您的个人概述...' }}</p>
            </div>
            <div class="preview-section">
              <h3 class="section-title">教育经历</h3>
              <p v-if="educations.length === 0" class="empty-tip">请在右侧添加您的教育经历...</p>
              <div v-for="(edu, index) in educations" :key="index" class="preview-item">
                <div class="item-header">
                  <strong>{{ edu.school }}</strong>
                  <span>{{ edu.startDate }} - {{ edu.endDate }}</span>
                </div>
                <div>{{ edu.degree }}，{{ edu.major }}</div>
              </div>
            </div>
            <div class="preview-section">
              <h3 class="section-title">工作经验</h3>
              <p v-if="experiences.length === 0" class="empty-tip">请在右侧添加您的工作经验...</p>
              <div v-for="(exp, index) in experiences" :key="index" class="preview-item">
                <div class="item-header">
                  <strong>{{ exp.company }}</strong>
                  <span>{{ exp.startDate }} - {{ exp.endDate }}</span>
                </div>
                <div>{{ exp.position }}</div>
                <div class="item-desc">{{ exp.description }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧编辑表单 -->
      <div class="resume-edit-panel">
        <el-tabs v-model="activeTab" type="card" class="edit-tabs">
          <el-tab-pane label="基本信息" name="basic">
            <el-form :model="basicInfo" :rules="basicInfoRules" ref="basicInfoForm" label-position="top">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="basicInfo.name" placeholder="请输入姓名" />
              </el-form-item>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="性别" prop="gender">
                    <el-select v-model="basicInfo.gender" placeholder="请选择性别" style="width: 100%">
                      <el-option label="男" value="male" />
                      <el-option label="女" value="female" />
                      <el-option label="保密" value="other" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="出生日期" prop="birthday">
                    <el-date-picker v-model="basicInfo.birthday" type="date" placeholder="选择日期" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="电话" prop="phone">
                    <el-input v-model="basicInfo.phone" placeholder="请输入联系电话" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="basicInfo.email" placeholder="请输入邮箱地址" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="期望职位" prop="jobTitle">
                <el-input v-model="basicInfo.jobTitle" placeholder="例如：前端开发工程师" />
              </el-form-item>
              <el-form-item label="个人优势" prop="summary">
                <el-input 
                  v-model="basicInfo.summary" 
                  type="textarea" 
                  rows="4"
                  maxlength="500"
                  show-word-limit
                  placeholder="请简要介绍自己的优势和特长，建议不超过500字" />
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="教育经历" name="education">
            <div class="section-header">
              <h3>教育经历</h3>
              <el-button type="primary" plain size="small" @click="addEducation">
                <i class="el-icon-plus"></i> 添加教育经历
              </el-button>
            </div>
            
            <el-empty v-if="educations.length === 0" description="暂无教育经历，请点击添加">
            </el-empty>
            
            <div v-for="(edu, index) in educations" :key="index" class="education-item">
              <div class="item-card">
                <div class="card-header">
                  <h4>{{ edu.school || '学校名称' }}</h4>
                  <div class="card-actions">
                    <i class="el-icon-edit" @click="editEducation(index)"></i>
                    <i class="el-icon-delete" @click="deleteEducation(index)"></i>
                  </div>
                </div>
                <div class="card-content">
                  <p><strong>专业：</strong>{{ edu.major }}</p>
                  <p><strong>学历：</strong>{{ edu.degree }}</p>
                  <p><strong>时间：</strong>{{ edu.startDate }} 至 {{ edu.endDate }}</p>
                </div>
              </div>
            </div>
            
            <!-- 教育经历表单对话框 -->
            <el-dialog :title="educationFormMode === 'add' ? '添加教育经历' : '编辑教育经历'" v-model="educationDialogVisible" width="500px">
              <el-form :model="educationForm" :rules="educationRules" ref="educationFormRef" label-position="top">
                <el-form-item label="学校" prop="school">
                  <el-input v-model="educationForm.school" placeholder="请输入学校名称" />
                </el-form-item>
                <el-form-item label="专业" prop="major">
                  <el-input v-model="educationForm.major" placeholder="请输入专业名称" />
                </el-form-item>
                <el-form-item label="学历" prop="degree">
                  <el-select v-model="educationForm.degree" placeholder="请选择学历" style="width: 100%">
                    <el-option label="高中" value="高中" />
                    <el-option label="大专" value="大专" />
                    <el-option label="本科" value="本科" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="博士" value="博士" />
                  </el-select>
                </el-form-item>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="入学时间" prop="startDate">
                      <el-date-picker v-model="educationForm.startDate" type="month" placeholder="选择入学时间" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="毕业时间" prop="endDate">
                      <el-date-picker v-model="educationForm.endDate" type="month" placeholder="选择毕业时间" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
              <template #footer>
                <el-button @click="educationDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveEducation">保存</el-button>
              </template>
            </el-dialog>
          </el-tab-pane>
          
          <el-tab-pane label="工作经验" name="experience">
            <div class="section-header">
              <h3>工作经验</h3>
              <el-button type="primary" plain size="small" @click="addExperience">
                <i class="el-icon-plus"></i> 添加工作经验
              </el-button>
            </div>
            
            <el-empty v-if="experiences.length === 0" description="暂无工作经验，请点击添加">
            </el-empty>
            
            <div v-for="(exp, index) in experiences" :key="index" class="experience-item">
              <div class="item-card">
                <div class="card-header">
                  <h4>{{ exp.company || '公司名称' }}</h4>
                  <div class="card-actions">
                    <i class="el-icon-edit" @click="editExperience(index)"></i>
                    <i class="el-icon-delete" @click="deleteExperience(index)"></i>
                  </div>
                </div>
                <div class="card-content">
                  <p><strong>职位：</strong>{{ exp.position }}</p>
                  <p><strong>时间：</strong>{{ exp.startDate }} 至 {{ exp.endDate }}</p>
                  <p><strong>工作描述：</strong>{{ exp.description }}</p>
                </div>
              </div>
            </div>
            
            <!-- 工作经验表单对话框 -->
            <el-dialog :title="experienceFormMode === 'add' ? '添加工作经验' : '编辑工作经验'" v-model="experienceDialogVisible" width="500px">
              <el-form :model="experienceForm" :rules="experienceRules" ref="experienceFormRef" label-position="top">
                <el-form-item label="公司名称" prop="company">
                  <el-input v-model="experienceForm.company" placeholder="请输入公司名称" />
                </el-form-item>
                <el-form-item label="职位名称" prop="position">
                  <el-input v-model="experienceForm.position" placeholder="请输入职位名称" />
                </el-form-item>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="入职时间" prop="startDate">
                      <el-date-picker v-model="experienceForm.startDate" type="month" placeholder="选择入职时间" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="离职时间" prop="endDate">
                      <el-date-picker v-model="experienceForm.endDate" type="month" placeholder="选择离职时间" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="工作描述" prop="description">
                  <el-input 
                    v-model="experienceForm.description" 
                    type="textarea" 
                    rows="4"
                    maxlength="500"
                    show-word-limit
                    placeholder="请详细描述您的工作职责和成就" />
                </el-form-item>
              </el-form>
              <template #footer>
                <el-button @click="experienceDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveExperience">保存</el-button>
              </template>
            </el-dialog>
          </el-tab-pane>
          
          <el-tab-pane label="项目经历" name="project">
            <div class="coming-soon">
              <el-empty description="项目经历功能即将上线，敬请期待！">
              </el-empty>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="技能特长" name="skills">
            <div class="coming-soon">
              <el-empty description="技能特长功能即将上线，敬请期待！">
              </el-empty>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'ResumeCreate',
  setup() {
    const router = useRouter()
    const activeTab = ref('basic')
    const basicInfoForm = ref(null)
    const educationFormRef = ref(null)
    const experienceFormRef = ref(null)
    const avatarUrl = ref('')
    
    // 基本信息表单数据
    const basicInfo = reactive({
      name: '',
      gender: '',
      birthday: '',
      phone: '',
      email: '',
      jobTitle: '',
      summary: ''
    })
    
    // 教育经历数据
    const educations = ref([])
    const educationDialogVisible = ref(false)
    const educationFormMode = ref('add')
    const currentEducationIndex = ref(-1)
    const educationForm = reactive({
      school: '',
      major: '',
      degree: '',
      startDate: '',
      endDate: ''
    })
    
    // 工作经验数据
    const experiences = ref([])
    const experienceDialogVisible = ref(false)
    const experienceFormMode = ref('add')
    const currentExperienceIndex = ref(-1)
    const experienceForm = reactive({
      company: '',
      position: '',
      startDate: '',
      endDate: '',
      description: ''
    })
    
    // 基本信息表单验证规则
    const basicInfoRules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      jobTitle: [
        { required: true, message: '请输入期望职位', trigger: 'blur' }
      ]
    }
    
    // 教育经历表单验证规则
    const educationRules = {
      school: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
      major: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
      degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
      startDate: [{ required: true, message: '请选择入学时间', trigger: 'change' }],
      endDate: [{ required: true, message: '请选择毕业时间', trigger: 'change' }]
    }
    
    // 工作经验表单验证规则
    const experienceRules = {
      company: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
      position: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
      startDate: [{ required: true, message: '请选择入职时间', trigger: 'change' }],
      endDate: [{ required: true, message: '请选择离职时间', trigger: 'change' }],
      description: [{ required: true, message: '请输入工作描述', trigger: 'blur' }]
    }
    
    // 返回上一页
    const goBack = () => {
      router.push('/dashboard')
    }
    
    // 处理头像上传
    const handleAvatarUpload = (e) => {
      const file = e.target.files[0]
      if (file) {
        // 检查文件类型
        if (!file.type.includes('image/')) {
          ElMessage.error('请上传图片文件')
          return
        }
        
        // 限制文件大小为2MB
        if (file.size > 2 * 1024 * 1024) {
          ElMessage.error('图片大小不能超过2MB')
          return
        }
        
        // 创建文件读取器
        const reader = new FileReader()
        reader.onload = (e) => {
          avatarUrl.value = e.target.result
          // 这里可以添加上传头像到服务器的逻辑
        }
        reader.readAsDataURL(file)
      }
    }
    
    // 格式化日期
    const formatDate = (date) => {
      if (!date) return ''
      if (typeof date === 'string') return date
      return format(date, 'yyyy-MM')
    }
    
    // 添加教育经历
    const addEducation = () => {
      educationFormMode.value = 'add'
      Object.keys(educationForm).forEach(key => {
        educationForm[key] = ''
      })
      educationDialogVisible.value = true
    }
    
    // 编辑教育经历
    const editEducation = (index) => {
      educationFormMode.value = 'edit'
      currentEducationIndex.value = index
      const edu = educations.value[index]
      Object.keys(educationForm).forEach(key => {
        educationForm[key] = edu[key]
      })
      educationDialogVisible.value = true
    }
    
    // 删除教育经历
    const deleteEducation = (index) => {
      ElMessage.confirm('确定要删除这条教育经历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        educations.value.splice(index, 1)
        ElMessage.success('已删除')
      }).catch(() => {})
    }
    
    // 保存教育经历
    const saveEducation = () => {
      educationFormRef.value.validate((valid) => {
        if (valid) {
          const formattedEducation = {
            ...educationForm,
            startDate: formatDate(educationForm.startDate),
            endDate: formatDate(educationForm.endDate)
          }
          
          if (educationFormMode.value === 'add') {
            educations.value.push(formattedEducation)
          } else {
            educations.value[currentEducationIndex.value] = formattedEducation
          }
          educationDialogVisible.value = false
          ElMessage.success(educationFormMode.value === 'add' ? '添加成功' : '修改成功')
        }
      })
    }
    
    // 添加工作经验
    const addExperience = () => {
      experienceFormMode.value = 'add'
      Object.keys(experienceForm).forEach(key => {
        experienceForm[key] = ''
      })
      experienceDialogVisible.value = true
    }
    
    // 编辑工作经验
    const editExperience = (index) => {
      experienceFormMode.value = 'edit'
      currentExperienceIndex.value = index
      const exp = experiences.value[index]
      Object.keys(experienceForm).forEach(key => {
        experienceForm[key] = exp[key]
      })
      experienceDialogVisible.value = true
    }
    
    // 删除工作经验
    const deleteExperience = (index) => {
      ElMessage.confirm('确定要删除这条工作经验吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        experiences.value.splice(index, 1)
        ElMessage.success('已删除')
      }).catch(() => {})
    }
    
    // 保存工作经验
    const saveExperience = () => {
      experienceFormRef.value.validate((valid) => {
        if (valid) {
          const formattedExperience = {
            ...experienceForm,
            startDate: formatDate(experienceForm.startDate),
            endDate: formatDate(experienceForm.endDate)
          }
          
          if (experienceFormMode.value === 'add') {
            experiences.value.push(formattedExperience)
          } else {
            experiences.value[currentExperienceIndex.value] = formattedExperience
          }
          experienceDialogVisible.value = false
          ElMessage.success(experienceFormMode.value === 'add' ? '添加成功' : '修改成功')
        }
      })
    }
    
    // 预览简历
    const previewResume = () => {
      ElMessage.info('预览功能开发中，敬请期待！')
    }
    
    // 保存简历
    const saveResume = () => {
      basicInfoForm.value.validate((valid) => {
        if (valid) {
          // 这里可以添加保存简历的API调用
          ElMessage.success('简历保存成功！')
        } else {
          activeTab.value = 'basic'
          ElMessage.warning('请先完善基本信息')
        }
      })
    }
    
    return {
      activeTab,
      avatarUrl,
      basicInfoForm,
      educationFormRef,
      experienceFormRef,
      basicInfo,
      educations,
      experiences,
      educationForm,
      experienceForm,
      basicInfoRules,
      educationRules,
      experienceRules,
      educationDialogVisible,
      experienceDialogVisible,
      educationFormMode,
      experienceFormMode,
      goBack,
      handleAvatarUpload,
      addEducation,
      editEducation,
      deleteEducation,
      saveEducation,
      addExperience,
      editExperience,
      deleteExperience,
      saveExperience,
      previewResume,
      saveResume
    }
  }
}
</script>

<style scoped>
/* 主容器样式 */
.resume-create-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
  min-height: 100vh;
  background-color: #f6f6f8;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.resume-header-left {
  display: flex;
  align-items: center;
}

.back-icon {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
  color: #666;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.resume-header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.preview-btn {
  color: #5384ff;
}

.save-btn {
  background-color: #5384ff;
  border-color: #5384ff;
}

/* 主体内容 */
.resume-content {
  display: flex;
  flex: 1;
  padding: 30px;
  gap: 30px;
}

/* 左侧预览面板 */
.resume-preview-panel {
  flex: 0 0 380px;
  width: 380px;
  position: sticky;
  top: 80px;
  height: calc(100vh - 150px);
  overflow-y: auto;
}

.preview-card {
  background-color: #fff;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.preview-header {
  display: flex;
  margin-bottom: 25px;
}

.preview-avatar {
  width: 80px;
  height: 80px;
  position: relative;
  margin-right: 20px;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
}

.avatar-upload {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 25px;
  height: 25px;
  border-radius: 50%;
  background-color: #5384ff;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.avatar-input {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.preview-basic-info {
  flex: 1;
}

.preview-basic-info h2 {
  font-size: 20px;
  margin: 0 0 5px 0;
  color: #333;
}

.preview-basic-info p {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 10px;
  font-size: 13px;
  color: #888;
}

.contact-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.preview-sections {
  padding-top: 10px;
}

.preview-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
  margin-bottom: 15px;
}

.preview-item {
  margin-bottom: 15px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.item-desc {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.empty-tip {
  color: #999;
  font-style: italic;
  font-size: 13px;
}

/* 右侧编辑面板 */
.resume-edit-panel {
  flex: 1;
  background-color: #fff;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.edit-tabs {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.item-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-header h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.card-actions i {
  font-size: 16px;
  cursor: pointer;
  color: #666;
}

.card-actions i:hover {
  color: #5384ff;
}

.card-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.card-content p {
  margin: 5px 0;
}

.education-item, .experience-item {
  margin-bottom: 20px;
}

.coming-soon {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
</style>
