import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import UserSettings from '../views/UserSettings.vue'
import AdminUsers from '../views/AdminUsers.vue'

// 路由规则
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true } // 需要登录
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/settings',
    name: 'UserSettings',
    component: UserSettings,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsers,
    meta: { 
      requiresAuth: true,
      roles: ['ADMIN']  // 修复：移除角色名前的空格
    }
  },
  {
    path: '/treemap',
    name: 'TreeMap',
    component: () => import('../views/TreeMap.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/treeList',
    name: 'TreeList',
    component: () => import('../views/TreeList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('../views/Report.vue'),
    meta: { requiresAuth: true }
},
  {
    path: '/:pathMatch(.*)*',
    redirect: '/' // 404页面重定向到首页
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：控制登录访问
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const token = localStorage.getItem('token')
  // 获取当前用户角色（从localStorage中读取，登录时应保存）
  const userRoles = JSON.parse(localStorage.getItem('roles') || '[]')

  if (requiresAuth) {
    if (!token) {
      // 未登录：跳转登录页
      next({ path: '/login', query: { redirect: to.fullPath } })
      ElMessage.warning('请先登录后再访问该页面')
    } else {
      // 已登录：检查角色权限（如果需要）
      const requiredRoles = to.meta.roles as string[]
      // 只有当路由需要特定角色且用户不具备该角色时才拦截
      if (requiredRoles && requiredRoles.length > 0 && 
          !requiredRoles.some(role => userRoles.includes(role))) {
        next('/') // 角色不符：跳首页
        ElMessage.error('没有权限访问该页面')
      } else {
        next() // 权限通过
      }
    }
  } else {
    // 无需登录的页面处理
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
  }
})

export default router