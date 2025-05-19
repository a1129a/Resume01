<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { useUserStore } from './store/user'
import { onMounted } from 'vue'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()
    
    onMounted(() => {
      // 如果已登录，获取最新的用户信息
      if (userStore.isAuthenticated) {
        userStore.fetchUserInfo()
      }
    })
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
  color: #2c3e50;
}

#app {
  min-height: 100vh;
}
</style>
