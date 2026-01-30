// src/utils/menu.ts
import { Component } from 'vue';

// 定义菜单类型
export interface MenuItem {
  index: string;          // 菜单唯一标识（对应el-menu的index）
  label: string;          // 菜单显示文本
  path?: string;          // 路由路径（点击跳转用）
  icon?: Component;       // 菜单图标（element-plus图标组件）
  children?: MenuItem[];  // 子菜单
  roles?: string[];       // 可见角色（不设置则所有登录用户可见）
  action?: string;        // 特殊操作（如logout，非路由跳转用）
}

// 全局菜单配置（按角色过滤）
export const allMenus: MenuItem[] = [
  // 主导航菜单
  {
    index: '1',
    label: '首页',
    path: '/',

  },
  
  {
    index: '2',
    label: '树木档案',
   
    children: [
      { index: '2-1', label: '树木地图', path: '/treemap' },
      { index: '2-2', label: '古树档案', path: '/treeList' },
      { index: '2-3', label: '统计分析', path: '/report' },
      { index: '2-4', label: '气象数据', path: '/weather' },
    ]
  },
  // 用户菜单（登录后显示）
  {
    index: 'user',
    label: '用户中心',
  
    children: [
      { 
        index: 'AdminSet', 
        label: '高级用户配置', 
        path: '/admin/users', 
        roles: ['ADMIN']  // 仅管理员可见
      },
      { index: 'settings', label: '用户设置', path: '/settings' },
      { index: 'logout', label: '退出登录', action: 'logout'}
    ]
  },
  // 未登录-登录按钮（特殊菜单项）
  {
    index: 'login',
    label: '登录',
    path: '/login',
   
    roles: ['GUEST']  // 仅未登录用户可见
  }
];

// 根据登录状态和角色过滤菜单
export const filterMenus = (isLogin: boolean, userRoles: string[]): MenuItem[] => {
  const filterSingleMenu = (menu: MenuItem): MenuItem | null => {
    // 1. 处理登录状态过滤（未登录只显示login菜单，登录隐藏login菜单）
    if (menu.roles?.includes('GUEST')) {
      return isLogin ? null : menu; // GUEST角色菜单：未登录显示，登录隐藏
    }
    if (!isLogin && menu.index !== 'login') {
      return null; // 未登录：只保留login菜单
    }

    // 2. 处理角色权限过滤（登录用户）
    if (menu.roles && !menu.roles.some(role => userRoles.includes(role))) {
      return null; // 角色不匹配：隐藏该菜单
    }

    // 3. 递归过滤子菜单
    if (menu.children && menu.children.length > 0) {
      const filteredChildren = menu.children
        .map(child => filterSingleMenu(child))
        .filter((child): child is MenuItem => child !== null);
      
      // 子菜单全部过滤后，父菜单也隐藏（避免空菜单）
      if (filteredChildren.length === 0) {
        return null;
      }
      return { ...menu, children: filteredChildren };
    }

    return menu;
  };

  // 执行过滤并移除null项
  return allMenus
    .map(menu => filterSingleMenu(menu))
    .filter((menu): menu is MenuItem => menu !== null);
};

// 根据路由路径匹配菜单index（用于高亮当前菜单）
export const getActiveMenuIndex = (path: string): string => {
  const findIndexByPath = (menus: MenuItem[]): string => {
    for (const menu of menus) {
      // 匹配当前路由的菜单
      if (menu.path === path) {
        return menu.index;
      }
      // 递归匹配子菜单
      if (menu.children) {
        const childIndex = findIndexByPath(menu.children);
        if (childIndex) {
          return childIndex;
        }
      }
    }
    return '1'; // 默认高亮首页
  };
  return findIndexByPath(allMenus);
};