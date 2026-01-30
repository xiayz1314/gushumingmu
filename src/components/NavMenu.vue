<template>
  <header class="menu-container">
    <!-- Logo与系统名称 -->
    <div class="logo-area" @click.stop>
      <img :src="logo" alt="系统logo" class="logo" />
      <span class="system-name">古树名木数智化管理系统</span>
    </div>

    <!-- 动态渲染菜单 -->
    <el-menu
      :default-active="activeIndex"
      class="main-menu"
      mode="horizontal"
      :ellipsis="false"
      @select="handleMenuSelect"
    >
      <template v-for="menu in filteredMenus" :key="menu.index">
        <!-- 带子菜单的菜单项 -->
        <el-sub-menu v-if="menu.children" :index="menu.index">
          <template #title>
            <component v-if="menu.icon" :is="menu.icon" class="menu-icon" />
            <span>{{ menu.label }}</span>
          </template>
          <!-- 子菜单渲染 -->
          <el-menu-item
            v-for="child in menu.children"
            :key="child.index"
            :index="child.index"
          >
            <component v-if="child.icon" :is="child.icon" class="menu-icon" />
            <span>{{ child.label }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 普通菜单项 -->
        <el-menu-item v-else :index="menu.index">
          <component v-if="menu.icon" :is="menu.icon" class="menu-icon" />
          <span>{{ menu.label }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </header>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import request from '../utils/request';
import logo from '@/assets/logo.svg';
// 导入菜单工具（核心：分离后的菜单配置和工具函数）
import { filterMenus, getActiveMenuIndex, MenuItem, allMenus } from '../utils/menu';

// 路由实例
const router = useRouter();
const route = useRoute();

// 状态管理
const isLogin = ref(false);
const fullName = ref('');
const userRoles = ref<string[]>([]);
const filteredMenus = ref<MenuItem[]>([]);

// 计算当前激活的菜单index（依赖menu.ts的工具函数）
const activeIndex = computed(() => {
  return getActiveMenuIndex(route.path);
});

// 更新菜单（核心：调用menu.ts的过滤函数）
const updateMenu = () => {
  const token = localStorage.getItem('token');
  const storedFullName = localStorage.getItem('fullName');
  const storedRoles = localStorage.getItem('roles');

  // 更新登录状态和角色
  isLogin.value = !!token;
  fullName.value = storedFullName || '';
  userRoles.value = storedRoles ? JSON.parse(storedRoles) : [];

  // 调用menu.ts的过滤函数获取可见菜单
  filteredMenus.value = filterMenus(isLogin.value, userRoles.value);
};

// 处理菜单点击
const handleMenuSelect = (key: string) => {
  // 查找点击的菜单配置
  const findMenu = (menus: MenuItem[]): MenuItem | null => {
    for (const menu of menus) {
      if (menu.index === key) return menu;
      if (menu.children) {
        const child = findMenu(menu.children);
        if (child) return child;
      }
    }
    return null;
  };

  const targetMenu = findMenu(filteredMenus.value);
  if (!targetMenu) return;

  // 处理退出登录
  if (targetMenu.action === 'logout') {
    handleLogout();
    return;
  }

  // 处理路由跳转
  if (targetMenu.path) {
    router.push(targetMenu.path);
  }
};

// 退出登录
const handleLogout = async () => {
  try {
    await request.post('/auth/logout');
    ElMessage.success('已成功退出登录');
  } catch (error) {
    ElMessage.warning('退出登录请求失败，但已清除本地状态');
  } finally {
    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('fullName');
    localStorage.removeItem('roles');
    // 更新菜单
    updateMenu();
    // 跳转登录页
    router.push('/login');
  }
};

// 监听登录状态变化事件（登录成功后触发）
const handleLoginStatusChange = () => {
  updateMenu();
};

// 初始化
onMounted(() => {
  updateMenu();
  // 监听localStorage变化（多标签页同步）
  window.addEventListener('storage', updateMenu);
  // 监听登录状态变化（当前标签页登录）
  window.addEventListener('loginStatusChanged', handleLoginStatusChange);
});

// 组件卸载时清理监听
onUnmounted(() => {
  window.removeEventListener('storage', updateMenu);
  window.removeEventListener('loginStatusChanged', handleLoginStatusChange);
});

// 监听路由变化，更新菜单高亮
watch(() => route.path, () => {
  // 依赖computed的activeIndex自动更新
});
</script>

<style scoped>
.menu-container {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 60px;
  width: 100%;
  box-sizing: border-box;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: auto;
  cursor: default;
}

.logo {
  width: 40px;
  height: 40px;
}

.system-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c6e36;
}

.main-menu {
  flex-shrink: 1;
  max-width: calc(100% - 260px);
}

.menu-icon {
  margin-right: 8px;
  width: 16px;
  height: 16px;
}

:deep(.el-menu--horizontal > .el-menu-item),
:deep(.el-menu--horizontal > .el-sub-menu) {
  margin-left: 20px;
  height: 60px;
  line-height: 60px;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #f5f7f2 !important;
}

/* 用户菜单右对齐 */
:deep(.el-menu--horizontal > .el-sub-menu[index="user"]) {
  margin-left: auto !important;
}
</style>