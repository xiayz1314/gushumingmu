<template>
  <div class="admin-users-container">
    <!-- 搜索和操作区 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-input
          v-model="searchUsername"
          placeholder="输入用户名搜索"
          clearable
          @keyup.enter="getUserList"
        >
          <template #append>
            <el-button type="primary" @click="getUserList">搜索</el-button>
          </template>
        </el-input>
      </el-col>
      <el-col :span="16" class="text-right">
        <el-button type="primary" @click="showAddUserDialog">新增用户</el-button>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <el-table
      :data="userList"
      border
      stripe
      style="width: 100%"
      :loading="tableLoading"
      ref="userTable"
    >
      <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
      <el-table-column prop="username" label="用户名" width="150"></el-table-column>
      <el-table-column prop="fullName" label="姓名" width="150"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
      <el-table-column prop="roles" label="角色" width="200">
        <template #default="scope">
          <el-tag v-for="role in scope.row.roles" :key="role" size="small" class="mr-1">
            {{ role }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column 
        prop="enabled" 
        label="状态" 
        width="100" 
        align="center"
      >
        <template #default="scope">
          <!-- 修复1：使用v-model双向绑定，实时同步状态 -->
          <el-switch
            v-model="statusMap[scope.row.id]"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="(val) => handleStatusChange(scope.row, val)"
            :disabled="isCurrentUser(scope.row)"
            :loading="statusChangeLoading[scope.row.id]"
          />
        </template>
      </el-table-column>
      <el-table-column 
        prop="createTime" 
        label="创建时间" 
        width="180"
        align="center"
      >
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" align="center">
        <template #default="scope">
          <el-button 
            size="small" 
            text 
            @click="handleEditUser(scope.row)"
            :disabled="isCurrentUser(scope.row)"
          >
            编辑
          </el-button>
          <el-button 
            size="small" 
            text 
            @click="handleResetPassword(scope.row)"
            :disabled="isCurrentUser(scope.row)"
            :loading="resetPwdLoading[scope.row.id]"
          >
            重置密码
          </el-button>
          <el-button 
            size="small" 
            text 
            @click="handleAssignRole(scope.row)"
            :disabled="isCurrentUser(scope.row)"
            :loading="assignRoleLoading[scope.row.id]"
          >
            分配角色
          </el-button>
          <el-button 
            size="small" 
            text 
            type="danger" 
            @click="handleDeleteUser(scope.row)"
            :disabled="isCurrentUser(scope.row)"
            :loading="deleteLoading[scope.row.id]"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      :page-sizes="[5, 10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="mt-4"
    />

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="userDialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
        :disabled="formLoading"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="userForm.fullName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" type="email" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" maxlength="11" />
        </el-form-item>
        <el-form-item 
          label="密码"
          prop="password"
          v-if="!isEditMode"
        >
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item 
          label="确认密码" 
          prop="confirmPassword"
          v-if="!isEditMode"
        >
          <el-input v-model="userForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="formLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog
      v-model="resetPwdDialogVisible"
      title="重置密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="pwdFormRef"
        :model="pwdForm"
        :rules="pwdFormRules"
        label-width="100px"
        :disabled="resetPwdLoading[currentUserId]"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPassword" :loading="resetPwdLoading[currentUserId]">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog
      v-model="assignRoleDialogVisible"
      title="分配角色"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="roleFormRef" :disabled="assignRoleLoading[currentUserId]">
        <el-form-item label="可选角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox 
              v-for="role in allRoles" 
              :key="role.id" 
              :label="role.roleName"
            >
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignRoleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignRole" :loading="assignRoleLoading[currentUserId]">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick, watch } from 'vue';
import { ElMessage, ElMessageBox, ElForm, ElTable } from 'element-plus';
import request from '@/utils/request';

// 日期格式化工具
const formatDate = (dateStr: string) => {
  if (!dateStr) return "-";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${padZero(date.getMonth() + 1)}-${padZero(date.getDate())} ${padZero(date.getHours())}:${padZero(date.getMinutes())}:${padZero(date.getSeconds())}`;
  } catch (error) {
    return dateStr;
  }
};

const padZero = (num: number) => num.toString().padStart(2, '0');

// 状态定义
const userList = ref([]);
const total = ref(0);
const page = ref(1);
const size = ref(10);
const searchUsername = ref('');
const tableLoading = ref(false);
const userTable = ref<InstanceType<typeof ElTable>>();

// 修复2：初始化statusMap为响应式对象，确保双向绑定生效
const statusMap = ref<Record<number, boolean>>({});

// 加载状态管理
const statusChangeLoading = ref<Record<number, boolean>>({});
const resetPwdLoading = ref<Record<number, boolean>>({});
const assignRoleLoading = ref<Record<number, boolean>>({});
const deleteLoading = ref<Record<number, boolean>>({});
const formLoading = ref(false);

// 弹窗状态
const userDialogVisible = ref(false);
const resetPwdDialogVisible = ref(false);
const assignRoleDialogVisible = ref(false);

// 当前操作的用户ID
const currentUserId = ref<number | null>(null);

// 表单数据
const userForm = reactive({
  username: '',
  fullName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
});

const pwdForm = reactive({
  newPassword: ''
});

// 角色数据
const allRoles = ref([]);
const selectedRoles = ref<string[]>([]);

// 表单引用
const userFormRef = ref<InstanceType<typeof ElForm>>();
const pwdFormRef = ref<InstanceType<typeof ElForm>>();

// 表单规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20之间', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: false, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次密码输入不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const pwdFormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
};

// 计算属性
const isEditMode = computed(() => !!currentUserId.value);
const userDialogTitle = computed(() => isEditMode.value ? '编辑用户' : '新增用户');

// 组件加载时获取数据
onMounted(() => {
  getUserList();
  getAllRoles();
});

// 修复3：使用immediate立即同步，避免初始状态空白
watch(userList, (newList) => {
  const newStatusMap = {} as Record<number, boolean>;
  newList.forEach((user: any) => {
    newStatusMap[user.id] = user.enabled;
  });
  statusMap.value = newStatusMap; // 直接赋值，触发响应式更新
  // 调试日志：确认状态同步
  const user3 = newList.find((u: any) => u.id === 3);
  if (user3) {
    console.log('用户3同步后状态：', newStatusMap[3]);
  }
}, { deep: true, immediate: true });

// 获取用户列表
const getUserList = async () => {
  tableLoading.value = true;
  try {
    const res = await request.get('/users', {
      page: page.value - 1,
      size: size.value,
      username: searchUsername.value || undefined,
      _t: Date.now() // 防止缓存
    });
    userList.value = res.data.content;
    total.value = res.data.totalElements;
  } catch (error: any) {
    console.error('获取用户列表失败', error);
  } finally {
    tableLoading.value = false;
  }
};

// 获取所有角色
const getAllRoles = async () => {
  try {
    const res = await request.get('/roles');
    allRoles.value = res.data;
  } catch (error: any) {
    console.error('获取角色列表失败', error);
  }
};

// 分页事件处理
const handleSizeChange = (val: number) => {
  size.value = val;
  getUserList();
};

const handleCurrentChange = (val: number) => {
  page.value = val;
  getUserList();
};

// 显示新增用户弹窗
const showAddUserDialog = () => {
  currentUserId.value = null;
  Object.assign(userForm, {
    username: '',
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: ''
  });
  userDialogVisible.value = true;
  nextTick(() => {
    userFormRef.value?.clearValidate();
  });
};

// 显示编辑用户弹窗
const handleEditUser = (user: any) => {
  currentUserId.value = user.id;
  Object.assign(userForm, {
    username: user.username,
    fullName: user.fullName,
    email: user.email,
    phone: user.phone,
    password: '',
    confirmPassword: ''
  });
  userDialogVisible.value = true;
  nextTick(() => {
    userFormRef.value?.clearValidate();
  });
};

// 提交用户表单
const submitUserForm = async () => {
  if (!userFormRef.value) return;
  
  try {
    await userFormRef.value.validate();
    formLoading.value = true;

    if (isEditMode.value && currentUserId.value) {
      await request.put(`/users/${currentUserId.value}`, {
        fullName: userForm.fullName,
        email: userForm.email,
        phone: userForm.phone
      });
      ElMessage.success('用户编辑成功');
    } else {
      await request.post('/auth/register', {
        username: userForm.username,
        fullName: userForm.fullName,
        email: userForm.email,
        phone: userForm.phone,
        password: userForm.password
      });
      ElMessage.success('用户创建成功');
    }
    userDialogVisible.value = false;
    getUserList();
  } catch (error: any) {
    if (error.name !== 'ValidationError') {
      console.error(isEditMode.value ? '编辑用户失败' : '创建用户失败', error);
    }  } finally {
    formLoading.value = false;
  }
};

// 处理状态切换（修复版：双向绑定+即时回滚）
const handleStatusChange = async (user: any, newStatus: boolean) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('不能修改当前登录用户的状态');
    // 修复4：立即回滚双向绑定的状态，避免UI误导
    statusMap.value[user.id] = !newStatus;
    return;
  }

  // 保存原始状态，用于接口失败回滚
  const originalStatus = !newStatus;
  statusChangeLoading.value[user.id] = true;

  try {
    // 发送状态更新请求（参数与后端DTO严格匹配）
    await request.put(`/users/${user.id}/status`, {
      enabled: newStatus
    });

    // 刷新列表确认状态同步
    await getUserList();
    ElMessage.success(newStatus ? '用户已启用' : '用户已禁用');
  } catch (error: any) {
    // 修复5：接口失败时强制回滚状态，确保UI与后端一致
    statusMap.value[user.id] = originalStatus;
    console.error('修改用户状态失败', error);
    ElMessage.error('修改状态失败，请重试');
  } finally {
    statusChangeLoading.value[user.id] = false;
  }
};

// 处理重置密码
const handleResetPassword = (user: any) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('请在个人中心修改自己的密码');
    return;
  }
  currentUserId.value = user.id;
  pwdForm.newPassword = '';
  resetPwdDialogVisible.value = true;
  nextTick(() => {
    pwdFormRef.value?.clearValidate();
  });
};

// 提交密码重置
const submitResetPassword = async () => {
  if (!currentUserId.value || !pwdFormRef.value) return;

  try {
    await pwdFormRef.value.validate();
    resetPwdLoading.value[currentUserId.value] = true;
    
    await request.put(`/users/${currentUserId.value}/password`, null, {
      params: { newPassword: pwdForm.newPassword }
    });
    ElMessage.success('密码重置成功');
    resetPwdDialogVisible.value = false;
  } catch (error: any) {
    if (error.name !== 'ValidationError') {
      console.error('重置密码失败', error);
    }
  } finally {
    if (currentUserId.value) {
      resetPwdLoading.value[currentUserId.value] = false;
    }
  }
};

// 处理分配角色
const handleAssignRole = async (user: any) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('不能修改当前登录用户的角色');
    return;
  }
  currentUserId.value = user.id;
  selectedRoles.value = [...(user.roles || [])];
  assignRoleDialogVisible.value = true;
};

// 提交角色分配
const submitAssignRole = async () => {
  if (!currentUserId.value) return;

  assignRoleLoading.value[currentUserId.value] = true;
  try {
    const user = userList.value.find((u: any) => u.id === currentUserId.value);
    if (!user) throw new Error('用户不存在');

    // 移除用户已拥有但未选中的角色
    const rolesToRemove = user.roles.filter((roleName: string) => 
      !selectedRoles.value.includes(roleName)
    );
    for (const roleName of rolesToRemove) {
      const role = allRoles.value.find((r: any) => r.roleName === roleName);
      if (role) {
        await request.delete(`/users/${currentUserId.value}/roles/${role.id}`);
      }
    }

    // 添加用户未拥有但已选中的角色
    const rolesToAdd = selectedRoles.value.filter((roleName: string) => 
      !user.roles.includes(roleName)
    );
    for (const roleName of rolesToAdd) {
      const role = allRoles.value.find((r: any) => r.roleName === roleName);
      if (role) {
        await request.post(`/users/${currentUserId.value}/roles/${role.id}`);
      }
    }

    ElMessage.success('角色分配成功');
    assignRoleDialogVisible.value = false;
    getUserList();
  } catch (error: any) {
    console.error('角色分配失败', error);
  } finally {
    if (currentUserId.value) {
      assignRoleLoading.value[currentUserId.value] = false;
    }
  }
};

// 处理删除用户
const handleDeleteUser = async (user: any) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('不能删除当前登录用户');
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除用户【${user.username}】吗？此操作不可恢复！`,
      '确认删除',
      {
        type: 'warning',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }
    );

    deleteLoading.value[user.id] = true;
    
    // 先移除用户所有角色关联
    if (user.roles && user.roles.length > 0) {
      for (const roleName of user.roles) {
        const role = allRoles.value.find((r: any) => r.roleName === roleName);
        if (role) {
          await request.delete(`/users/${user.id}/roles/${role.id}`);
        }
      }
    }

    // 删除用户本身
    await request.delete(`/users/${user.id}`);
    
    ElMessage.success('用户删除成功');
    getUserList();
  } catch (error: any) {
    if (error.message !== 'cancel') {
      console.error('删除用户失败', error);
    }
  } finally {
    deleteLoading.value[user.id] = false;
  }
};

// 工具方法：获取当前登录用户
const getCurrentUsername = () => {
  return localStorage.getItem('username') || '';
};

// 判断是否为当前登录用户（禁止操作自己）
const isCurrentUser = (user: any) => {
  return user.username === getCurrentUsername();
};
</script>

<style scoped>
.admin-users-container {
  padding: 20px;
}

::v-deep .el-tag {
  margin-right: 4px;
}

::v-deep .el-dialog__body {
  max-height: 60vh;
  overflow-y: auto;
}

/* 优化开关加载状态样式，避免遮挡 */
::v-deep .el-switch__loading {
  z-index: 1;
}
</style>