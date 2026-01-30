<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>用户设置</h2>
      <p class="sub-title">管理您的个人信息和账户安全</p>
    </div>

    <el-card class="settings-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="个人信息" name="profile">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="120px"
            class="settings-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" maxlength="20" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" type="email" />
            </el-form-item>

            <el-form-item label="注册时间">
              <el-input v-model="profileForm.registerTime" disabled />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="profileLoading" @click="handleSaveProfile">保存修改</el-button>
              <el-button @click="resetProfileForm" style="margin-left:10px">取消</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="密码安全" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            class="settings-form"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">确认修改</el-button>
              <el-button @click="resetPasswordForm" style="margin-left:10px">取消</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElForm, ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()

// tabs
const activeTab = ref('profile')

// profile form
const profileFormRef = ref<InstanceType<typeof ElForm> | null>(null)
const profileLoading = ref(false)
const profileForm = reactive({
  username: '',
  email: '',
  registerTime: ''
})

const profileRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
})

// password form
const passwordFormRef = ref<InstanceType<typeof ElForm> | null>(null)
const passwordLoading = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = reactive({
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不少于6位', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '需包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 加载用户信息
const loadProfile = async () => {
  try {
    const res = await request.get('/user/profile')
    const data = res.data?.data || {}
    profileForm.username = data.username || ''
    profileForm.email = data.email || ''
    profileForm.registerTime = data.registerTime ? formatDate(data.registerTime) : ''
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.msg || '获取用户信息失败')
  }
}

// 保存个人信息
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return
  const valid = await profileFormRef.value.validate().catch(() => false)
  if (!valid) return
  profileLoading.value = true
  try {
    await request.put('/user/profile', {
      username: profileForm.username,
      email: profileForm.email
    })
    localStorage.setItem('username', profileForm.username)
    ElMessage.success('个人信息已更新')
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.msg || '更新失败，请重试')
  } finally {
    profileLoading.value = false
  }
}

// 修改密码（确认 + 调用接口 + 登出）
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  const confirmed = await ElMessageBox.confirm(
    '修改密码后将强制重新登录，是否继续？',
    '确认修改',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).catch(() => false)
  if (!confirmed) return

  passwordLoading.value = true
  try {
    await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，正在登出...')
    // 清除登录信息并跳转登录页
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    setTimeout(() => router.push({ name: 'Login' }), 800)
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.msg || '修改失败，请检查原密码')
  } finally {
    passwordLoading.value = false
  }
}

// 重置方法
const resetProfileForm = () => profileFormRef.value?.resetFields()
const resetPasswordForm = () => passwordFormRef.value?.resetFields()

const handleTabChange = (name: string) => {
  if (name === 'profile') {
    // 切回个人信息时重新加载以防数据被修改
    loadProfile()
  } else {
    // 清空密码表单
    resetPasswordForm()
  }
}

const formatDate = (time: number | string) => {
  const d = new Date(time)
  if (isNaN(d.getTime())) return ''
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.settings-page {
  max-width: 980px;
  margin: 24px auto;
  padding: 0 16px;
  box-sizing: border-box;
}

.page-header h2 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #2c3e50;
}

.sub-title {
  margin: 0 0 18px;
  color: #666;
}

.settings-card {
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  overflow: visible;
}

.settings-form {
  margin-top: 10px;
}

/* 保证表单内元素在窄屏下不会溢出 */
:deep(.el-form) { min-width: 0; }
</style>