<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 20px;
}

.not-found-content h1 {
  font-size: 6rem;
  color: #409EFF;
  margin: 0;
  line-height: 1;
}

.not-found-content h2 {
  font-size: 2rem;
  margin: 10px 0 20px;
  color: #303133;
}

.not-found-content p {
  margin-bottom: 30px;
  color: #606266;
}
</style>
