<template>
  <div class="auth-container">
    <div class="auth-card">
      <!-- 左侧介绍区 -->
      <div class="left-panel">
        <img src="/src/assets/logo.svg" alt="系统logo" class="logo" />
        <h2 class="app-title">古树名木数智化管理系统</h2>
        <p class="app-desc">专业的古树名木保护与管理解决方案，让每一棵古树得到精心呵护。</p>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-section">
        <h3 class="form-title">{{ isRegisterMode ? '注册账号' : '登录系统' }}</h3>

        <el-form
          ref="formRef"
          :model="form"
          :rules="isRegisterMode ? registerRules : loginRules"
          label-position="top"
          class="auth-form"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="用户名（4-20位字母/数字/下划线）" 
              clearable
              maxlength="20"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="密码（至少6位，含字母和数字）" 
              show-password
              clearable
              maxlength="20"
            />
          </el-form-item>

          <el-form-item v-if="isRegisterMode" prop="email">
            <el-input 
              v-model="form.email" 
              type="email" 
              placeholder="邮箱（用于密码找回）" 
              clearable
            />
          </el-form-item>
          
          <el-form-item v-if="isRegisterMode" prop="fullName">
            <el-input 
              v-model="form.fullName" 
              placeholder="姓名" 
              clearable
              maxlength="20"
            />
          </el-form-item>
          
          <el-form-item v-if="isRegisterMode" prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="联系电话" 
              clearable
              maxlength="11"
              @input="handlePhoneInput"
            />
          </el-form-item>

          <!-- 管理员注册密钥验证 -->
          <el-form-item 
            v-if="isRegisterMode && showAdminKeyInput" 
            prop="adminKey"
          >
            <el-input 
              v-model="form.adminKey" 
              type="password" 
              placeholder="管理员注册密钥（可选）" 
              clearable
            />
          </el-form-item>
          
          <div v-if="isRegisterMode" class="admin-key-toggle">
            <a href="#" @click.prevent="showAdminKeyInput = !showAdminKeyInput">
              {{ showAdminKeyInput ? '隐藏管理员注册' : '需要创建管理员账号？' }}
            </a>
          </div>

          <el-form-item>
            <el-button 
              type="success" 
              :loading="loading" 
              @click="handleSubmit" 
              style="width: 100%"
            >
              {{ isRegisterMode ? '注册并登录' : '立即登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="mode-switch">
          <a href="#" @click.prevent="toggleMode">
            {{ isRegisterMode ? '已有账号？去登录' : '还没有账号？免费注册' }}
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElForm, ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import request from '../utils/request'

// 路由实例
const router = useRouter()
const route = useRoute()

// 表单状态
const formRef = ref<InstanceType<typeof ElForm> | null>(null)
const loading = ref(false)
const isRegisterMode = ref(false)
const showAdminKeyInput = ref(false) // 控制管理员密钥输入框

// 表单数据
const form = reactive({
  username: '',
  password: '',
  email: '',
  fullName: '',
  phone: '',
  adminKey: '' // 管理员注册密钥
})

// 验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名需为4-20位字母/数字/下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' },
    // { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码需包含字母和数字', trigger: 'blur' }
  ]
})

const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名需为4-20位字母/数字/下划线', trigger: 'blur' },
    { 
      validator: async (rule, value, callback) => {
        if (value) {
          try {
            const res = await request.get('/api/users/check-username', { params: { username: value } })
            if (!res.data) callback(new Error('该用户名已被注册'))
            else callback()
          } catch (err) {
            callback(new Error('用户名校验失败，请重试'))
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码需包含字母和数字', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 20, message: '姓名长度不超过20位', trigger: 'blur' }
  ],
  phone: [
    { required: false, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  adminKey: [
    {
      validator: (rule, value, callback) => {
        const correctKey = import.meta.env.VITE_ADMIN_KEY || 'admin123'
        if (showAdminKeyInput.value && value !== correctKey) {
          callback(new Error('管理员密钥不正确'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 手机号输入处理（仅保留数字）
const handlePhoneInput = (value: string) => {
  form.phone = value.replace(/[^\d]/g, '')
}

// 切换登录/注册模式
const toggleMode = () => {
  // 重置表单
  Object.keys(form).forEach(key => {
    form[key as keyof typeof form] = ''
  })
  formRef.value?.clearValidate()
  showAdminKeyInput.value = false
  isRegisterMode.value = !isRegisterMode.value
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isRegisterMode.value) {
      // 注册逻辑：区分普通用户和管理员
      const isAdmin = showAdminKeyInput.value && 
        form.adminKey === (import.meta.env.VITE_ADMIN_KEY || 'admin123')
      
      await request.post('/auth/register', {
        ...form,
        role: isAdmin ? 'ADMIN' : 'USER' // 传递角色信息
      })
      
      ElMessage.success('注册成功，即将跳转到登录')
      setTimeout(toggleMode, 1500)
    } else {
      // 登录逻辑：获取令牌并通知菜单更新
      const res = await request.post('/auth/login', form)
      const { token, fullName, username, roles } = res.data
      
      // 保存登录状态
      localStorage.setItem('token', token)
      localStorage.setItem('fullName', fullName || '')
      localStorage.setItem('username', username || '')
      localStorage.setItem('roles', JSON.stringify(roles || ['USER']))
      
      // 发送登录状态变化事件（通知菜单更新）
      window.dispatchEvent(new CustomEvent('loginStatusChanged'))
      
      // 跳转原页面或首页
      const redirect = route.query.redirect as string
      router.push(redirect || '/')
      ElMessage.success(`欢迎回来，${fullName || username}！`)
    }
  } catch (err: any) {
    let errorMsg = '操作失败，请重试'
    if (err.response) errorMsg = err.response.data?.msg || errorMsg
    else if (err.message?.includes('Network Error')) errorMsg = '网络连接失败，请检查网络'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

// 已登录用户直接跳转首页
onMounted(() => {
  if (localStorage.getItem('token')) {
    router.push('/')
  }
})
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #f0f7f2;
  box-sizing: border-box;
}

.auth-card {
  width: min(900px, 100%);
  display: grid;
  grid-template-columns: 1fr 1fr;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.08);
  background: #fff;
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.left-panel {
  padding: 40px;
  background: linear-gradient(180deg, #2f8a4a 0%, #2b7f44 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: center;
  @media (max-width: 768px) {
    display: none;
  }
}

.logo {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  background: #fff;
  padding: 8px;
  object-fit: contain;
}

.app-title {
  font-size: 24px;
  margin: 0;
  font-weight: 600;
}

.app-desc {
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  line-height: 1.6;
}

.form-section {
  padding: 40px;
  box-sizing: border-box;
}

.form-title {
  margin: 0 0 24px 0;
  text-align: center;
  font-size: 22px;
  color: #333;
}

.auth-form {
  margin-bottom: 16px;
}

.admin-key-toggle {
  text-align: right;
  margin: -10px 0 16px 0;
  font-size: 12px;
}

.admin-key-toggle a {
  color: #409eff;
  text-decoration: none;
}

.admin-key-toggle a:hover {
  text-decoration: underline;
}

.mode-switch {
  text-align: center;
}

.mode-switch a {
  color: #409eff;
  cursor: pointer;
  text-decoration: none;
}

.mode-switch a:hover {
  text-decoration: underline;
}

/* 输入框样式优化 */
::v-deep .el-input__input:focus {
  border-color: #2f8a4a !important;
  box-shadow: 0 0 0 2px rgba(47, 138, 74, 0.2) !important;
}
</style>