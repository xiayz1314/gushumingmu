<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <NavMenu />
    </el-header>

    <el-main class="global-container">
      <!-- 包一层 page-wrapper，便于控制每个页面如何展示 -->
      <div class="page-wrapper">
        <router-view />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import NavMenu from './components/NavMenu.vue'
import router from './router';

</script>

<style>
/* 根级安全盒模型与高度（保留） */
html, body, #app {
  box-sizing: border-box;
  height: 100%;
  margin: 0;
  padding: 0;
}
*, *::before, *::after { box-sizing: inherit; }

/* el-container 填满视口，el-main 可滚动 */
.app-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* header 高度统一（与页面 margin-top 对齐） */
.app-header {
  height: 60px;
  flex: 0 0 60px;
}

/* 主区：允许内部收缩，避免横向溢出 */
.global-container {
  flex: 1 1 auto;
  overflow: auto;
  padding: 0; /* 页面内部自行处理内边距 */
  min-width: 0; /* 关键：允许子元素在 flex 中收缩，防止右侧空白 */
}

/* router-view 包装器：默认流式铺满，可在子页里居中或使用 grid */
.page-wrapper {
  width: 100%;
  max-width: none;
  box-sizing: border-box;
  min-width: 0; /* 关键 */
  padding: 20px; /* 统一内边距，可按需调整 */
}

/* 清理可能造成计算异常的全局重置（保持简单） */
body, #app { background: #fafbfc; font-family: Inter, system-ui, sans-serif; }
</style>