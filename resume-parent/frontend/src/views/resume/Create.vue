<template>
  <div class="resume-create-container">
    <el-page-header content="创建新简历" @back="goBack" />
    
    <div class="resume-form-container">
      <el-steps :active="activeStep" finish-status="success" simple>
        <el-step title="基本信息" />
        <el-step title="教育经历" />
        <el-step title="工作经验" />
        <el-step title="项目经历" />
        <el-step title="技能特长" />
        <el-step title="选择模板" />
      </el-steps>
      
      <div class="step-content">
        <!-- 第一步：基本信息 -->
        <el-form 
          v-if="activeStep === 0" 
          :model="basicInfo" 
          :rules="basicInfoRules" 
          ref="basicInfoForm"
          label-position="top">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="basicInfo.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="basicInfo.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="male" />
                  <el-option label="女" value="female" />
                  <el-option label="保密" value="other" />
                </el-select>
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
          
          <el-form-item label="求职意向" prop="jobTitle">
            <el-input v-model="basicInfo.jobTitle" placeholder="例如：前端开发工程师" />
          </el-form-item>
          
          <el-form-item label="个人概述" prop="summary">
            <el-input 
              v-model="basicInfo.summary" 
              type="textarea" 
              rows="4"
              placeholder="简要介绍自己，突出优势和特长" />
          </el-form-item>
        </el-form>
        
        <!-- 其他步骤 -->
        <div v-else class="coming-soon">
          <el-empty description="此功能正在开发中，敬请期待！">
            <el-button type="primary" @click="activeStep = 0">返回基本信息</el-button>
          </el-empty>
        </div>
      </div>
      
      <div class="step-actions">
        <el-button v-if="activeStep > 0" @click="previousStep">上一步</el-button>
        <el-button 
          type="primary" 
          v-if="activeStep < 5" 
          @click="nextStep">
          {{ activeStep === 0 ? '保存并继续' : '下一步' }}
        </el-button>
        <el-button 
          type="success" 
          v-if="activeStep === 5" 
          @click="createResume">
          完成创建
        </el-button>
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
    const activeStep = ref(0)
    const basicInfoForm = ref(null)
    
    // 基本信息表单数据
    const basicInfo = reactive({
      name: '',
      gender: '',
      phone: '',
      email: '',
      jobTitle: '',
      summary: ''
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
        { required: true, message: '请输入求职意向', trigger: 'blur' }
      ]
    }
    
    // 返回上一页
    const goBack = () => {
      router.push('/dashboard')
    }
    
    // 上一步
    const previousStep = () => {
      if (activeStep.value > 0) {
        activeStep.value--
      }
    }
    
    // 下一步
    const nextStep = () => {
      if (activeStep.value === 0) {
        // 验证基本信息表单
        basicInfoForm.value.validate(valid => {
          if (valid) {
            activeStep.value++
            
            // 这里可以添加保存基本信息的API调用
            ElMessage.success('基本信息已保存')
          } else {
            ElMessage.warning('请完成必填项')
          }
        })
      } else if (activeStep.value < 5) {
        activeStep.value++
      }
    }
    
    // 创建简历
    const createResume = () => {
      ElMessage.info('简历创建功能正在开发中，敬请期待！')
    }
    
    return {
      activeStep,
      basicInfoForm,
      basicInfo,
      basicInfoRules,
      goBack,
      previousStep,
      nextStep,
      createResume
    }
  }
}
</script>

<style scoped>
.resume-create-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}

.resume-form-container {
  margin-top: 30px;
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.step-content {
  margin: 40px 0;
  min-height: 300px;
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.coming-soon {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
</style>
