<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1>简历一键生成与投递助手</h1>
        <p class="hero-subtitle">提升简历质量，一键多平台投递，助力您的求职之旅</p>
        <div class="hero-buttons">
          <el-button type="primary" size="large" @click="$router.push('/login')">免费使用</el-button>
          <el-button size="large" @click="$router.push('/login')">立即登录</el-button>
        </div>
      </div>
    </div>

    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <el-row :gutter="30">
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-document feature-icon"></i>
            <h3>智能简历生成</h3>
            <p>根据您的个人信息、教育经历和工作经验，自动生成专业简历</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-magic-stick feature-icon"></i>
            <h3>多种精美模板</h3>
            <p>提供多种行业专用模板，适合不同职位和风格需求</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-upload feature-icon"></i>
            <h3>一键多平台投递</h3>
            <p>支持同时投递到多个招聘平台，提高求职效率</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-chat-line-round feature-icon"></i>
            <h3>AI简历优化建议</h3>
            <p>通过AI分析，为您的简历提供专业优化建议</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-data-analysis feature-icon"></i>
            <h3>求职数据分析</h3>
            <p>跟踪简历投递状态，分析求职效果</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="feature-card">
            <i class="el-icon-mobile feature-icon"></i>
            <h3>多设备同步</h3>
            <p>电脑、手机多端同步，随时随地管理简历</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="how-it-works">
      <h2 class="section-title">使用流程</h2>
      <el-steps :active="1" finish-status="success" simple>
        <el-step title="快捷登录" description="手机号或微信一键登录"></el-step>
        <el-step title="填写资料" description="输入个人信息和经历"></el-step>
        <el-step title="选择模板" description="挑选适合的简历模板"></el-step>
        <el-step title="一键投递" description="将简历投递到多个平台"></el-step>
      </el-steps>
    </div>

    <div class="cta-section">
      <h2>立即开始您的求职之旅</h2>
      <p>加入我们，让求职变得更简单、更高效</p>
      <el-button type="primary" size="large" @click="$router.push('/login')">免费使用</el-button>
    </div>

    <footer class="home-footer">
      <p>© 2025 简历一键生成与投递助手 - 所有权利保留</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home-container {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.hero-section {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
  padding: 80px 20px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-content h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 40px;
  color: #303133;
  position: relative;
  padding-bottom: 15px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background-color: #1890ff;
}

.features-section {
  padding: 80px 20px;
  background-color: #f5f7fa;
}

.feature-card {
  background: white;
  border-radius: 8px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  height: 100%;
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 3rem;
  color: #1890ff;
  margin-bottom: 20px;
}

.feature-card h3 {
  margin-bottom: 15px;
  color: #303133;
}

.how-it-works {
  padding: 80px 20px;
  background-color: white;
}

.cta-section {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
  padding: 60px 20px;
  text-align: center;
}

.cta-section h2 {
  margin-bottom: 15px;
}

.cta-section p {
  margin-bottom: 30px;
  opacity: 0.9;
}

.home-footer {
  background-color: #263238;
  color: white;
  text-align: center;
  padding: 20px;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
