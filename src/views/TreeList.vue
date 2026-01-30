<template>
  <div class="ancient-tree-management">
    <!-- 页面标题 -->
    <el-page-header 
      @back="handleBack" 
      content="古树信息管理"
    />

    <!-- 操作按钮区 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-input 
          v-model="searchForm.treeName" 
          placeholder="请输入古树名称" 
          clearable
          prefix-icon="Search"
          class="mr-2"
        />
        <el-input 
          v-model="searchForm.species" 
          placeholder="请输入树种" 
          clearable
          prefix-icon="Search"
          class="mr-2"
        />
        <el-button type="primary" @click="getAncientTreePage">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-col>
      <el-col :span="16" class="text-right">
        <el-button 
          type="primary" 
          @click="handleAdd" 
          v-if="hasCreatePermission"
          icon="Plus"
        >
          新增古树信息
        </el-button>
      </el-col>
    </el-row>

    <!-- 古树列表 -->
    <el-table 
      :data="treePage.records" 
      border 
      stripe 
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="treeCode" label="古树编码" />
      <el-table-column prop="treeName" label="古树名称" />
      <el-table-column prop="species" label="树种" />
      <el-table-column prop="age" label="树龄" />
      <el-table-column prop="healthStatus" label="健康状态">
        <template #default="scope">
          <el-tag 
            :type="scope.row.healthStatus === 1 ? 'success' : 
                   scope.row.healthStatus === 2 ? 'warning' : 'danger'"
          >
            {{ scope.row.healthStatus === 1 ? '健康' : 
               scope.row.healthStatus === 2 ? '一般' : '濒危' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="protectionLevel" label="保护级别">
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.protectionLevel)">
            {{ scope.row.protectionLevel === 1 ? '一级' : 
               scope.row.protectionLevel === 2 ? '二级' : '三级' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button 
            size="small" 
            @click="handleView(scope.row.id)"
            icon="Eye"
          >
            查看
          </el-button>
          <el-button 
            size="small" 
            type="primary" 
            @click="handleEdit(scope.row.id)"
            icon="Edit"
            v-if="hasUpdatePermission"
          >
            编辑
          </el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="handleDelete(scope.row.id)"
            icon="Delete"
            v-if="hasDeletePermission"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="treePage.total > 0"
      :current-page="pagination.page"
      :page-size="pagination.size"
      :page-sizes="[5, 10, 20, 50]"
      :total="treePage.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      :width="dialogWidth"
      destroy-on-close
    >
      <el-form 
        :model="formData" 
        :rules="formRules" 
        ref="formRef"
        label-width="120px"
      >
        <el-form-item 
          label="古树编码" 
          prop="treeCode"
          v-if="dialogType === 'add'"
        >
          <el-input v-model="formData.treeCode" />
        </el-form-item>
        <el-form-item label="古树名称" prop="treeName">
          <el-input v-model="formData.treeName" />
        </el-form-item>
        <el-form-item label="树种" prop="species">
          <el-input v-model="formData.species" />
        </el-form-item>
        <el-form-item label="树龄" prop="age">
          <el-input v-model.number="formData.age" type="number" />
        </el-form-item>
        <el-form-item label="年龄估算类型" prop="ageEstimateType">
          <el-select v-model="formData.ageEstimateType" placeholder="请选择">
            <el-option label="精确" value="1" />
            <el-option label="估算" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="胸径(米)" prop="diameter">
          <el-input v-model.number="formData.diameter" type="number" step="0.01" />
        </el-form-item>
        <el-form-item label="树高(米)" prop="height">
          <el-input v-model.number="formData.height" type="number" step="0.01" />
        </el-form-item>
        <el-form-item label="冠幅(米)" prop="crownWidth">
          <el-input v-model.number="formData.crownWidth" type="number" step="0.01" />
        </el-form-item>
        <el-form-item label="健康状态" prop="healthStatus">
          <el-select v-model="formData.healthStatus" placeholder="请选择">
            <el-option label="健康" value="1" />
            <el-option label="一般" value="2" />
            <el-option label="濒危" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="保护级别" prop="protectionLevel">
          <el-select v-model="formData.protectionLevel" placeholder="请选择">
            <el-option label="一级" value="1" />
            <el-option label="二级" value="2" />
            <el-option label="三级" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model.number="formData.longitude" type="number" step="0.000001" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model.number="formData.latitude" type="number" step="0.000001" />
        </el-form-item>
        <el-form-item label="详细位置" prop="locationDetail">
          <el-input v-model="formData.locationDetail" />
        </el-form-item>
        <el-form-item label="权属" prop="ownership">
          <el-input v-model="formData.ownership" />
        </el-form-item>
        <el-form-item label="管理人" prop="manager">
          <el-input v-model="formData.manager" />
        </el-form-item>
        <el-form-item label="联系电话" prop="managerPhone">
          <el-input v-model="formData.managerPhone" />
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog 
      title="古树详情" 
      v-model="detailVisible" 
      width="60%"
      destroy-on-close
    >
      <el-descriptions column="1" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="古树编码">{{ detailData.treeCode }}</el-descriptions-item>
        <el-descriptions-item label="古树名称">{{ detailData.treeName }}</el-descriptions-item>
        <el-descriptions-item label="树种">{{ detailData.species }}</el-descriptions-item>
        <el-descriptions-item label="树龄">{{ detailData.age }}</el-descriptions-item>
        <el-descriptions-item label="年龄估算类型">
          {{ detailData.ageEstimateType === 1 ? '精确' : '估算' }}
        </el-descriptions-item>
        <el-descriptions-item label="胸径(米)">{{ detailData.diameter }}</el-descriptions-item>
        <el-descriptions-item label="树高(米)">{{ detailData.height }}</el-descriptions-item>
        <el-descriptions-item label="冠幅(米)">{{ detailData.crownWidth }}</el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag 
            :type="detailData.healthStatus === 1 ? 'success' : 
                   detailData.healthStatus === 2 ? 'warning' : 'danger'"
          >
            {{ detailData.healthStatus === 1 ? '健康' : 
               detailData.healthStatus === 2 ? '一般' : '濒危' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="保护级别">
          <el-tag :type="getLevelType(detailData.protectionLevel)">
            {{ detailData.protectionLevel === 1 ? '一级' : 
               detailData.protectionLevel === 2 ? '二级' : '三级' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="经纬度">
          {{ detailData.longitude }}, {{ detailData.latitude }}
        </el-descriptions-item>
        <el-descriptions-item label="详细位置">{{ detailData.locationDetail }}</el-descriptions-item>
        <el-descriptions-item label="权属">{{ detailData.ownership }}</el-descriptions-item>
        <el-descriptions-item label="管理人">{{ detailData.manager }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.managerPhone }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 获取全局实例和API
const { proxy } = getCurrentInstance()
const api = proxy.$api.ancientTree  // 假设已配置API请求实例

// 权限控制（根据实际登录用户角色判断）
const hasCreatePermission = ref(true)  // ADMIN, MANAGER可操作
const hasUpdatePermission = ref(true)  // ADMIN, MANAGER可操作
const hasDeletePermission = ref(true)  // 仅ADMIN可操作

// 搜索表单
const searchForm = reactive({
  treeName: '',
  species: ''
})

// 分页参数 — 注意：UI 使用 1-based 页码
const pagination = reactive({
  page: 1,   // 修复：初始设置为 1（el-pagination 期望 >=1）
  size: 10,
  total: 0
})

// 表格数据，保证 records 字段存在
const treePage = ref({
  records: [],
  total: 0
})
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref('add')  // add/edit
const dialogWidth = ref('60%')

// 表单数据
const formData = reactive({
  id: null,
  treeCode: '',
  treeName: '',
  species: '',
  age: null,
  ageEstimateType: null,
  diameter: null,
  height: null,
  crownWidth: null,
  healthStatus: null,
  protectionLevel: null,
  longitude: null,
  latitude: null,
  locationDetail: '',
  ownership: '',
  manager: '',
  managerPhone: '',
  description: ''
})

// 详情数据
const detailData = ref({})

// 表单验证规则
const formRules = reactive({
  treeCode: [
    { required: true, message: '请输入古树编码', trigger: 'blur' }
  ],
  treeName: [
    { required: true, message: '请输入古树名称', trigger: 'blur' }
  ],
  species: [
    { required: true, message: '请输入树种', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入树龄', trigger: 'blur' },
    { type: 'number', min: 1, message: '树龄必须为正整数', trigger: 'blur' }
  ],
  ageEstimateType: [
    { required: true, message: '请选择年龄估算类型', trigger: 'change' }
  ],
  healthStatus: [
    { required: true, message: '请选择健康状态', trigger: 'change' }
  ],
  protectionLevel: [
    { required: true, message: '请选择保护级别', trigger: 'change' }
  ]
})

// 表单引用
const formRef = ref(null)

// 页面加载时获取列表
onMounted(() => {
  getAncientTreePage()
  // 实际项目中应根据用户角色动态设置权限
  // checkPermissions()
})

// 获取古树列表
const getAncientTreePage = async () => {
  loading.value = true
  try {
    // 向后端发送 0-based page（后端常见约定），UI 使用 1-based
    const reqPage = Math.max(0, pagination.page - 1)
    const res = await api.getAncientTreePage(reqPage, pagination.size, searchForm.treeName, searchForm.species)
    if (res && res.code === 200) {
      // 兼容后端分页字段：records 或 content 或 data.records/content
      const pageData = res.data || {}
      const records = pageData.records || pageData.content || []
      const total = pageData.total || pageData.totalElements || 0

      treePage.value = { records, total }
      pagination.total = total
    } else {
      ElMessage.error(res?.msg || '获取数据失败')
      treePage.value = { records: [], total: 0 }
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取古树列表失败', error)
    ElMessage.error('网络错误，获取数据失败')
    treePage.value = { records: [], total: 0 }
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.treeName = ''
  searchForm.species = ''
  pagination.page = 1
  getAncientTreePage()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1   // 回到第一页
  getAncientTreePage()
}

// 页码改变（接收 el-pagination 的 1-based 页码）
const handleCurrentChange = (page) => {
  pagination.page = page
  getAncientTreePage()
}

// 返回
const handleBack = () => {
  proxy.$router.back()
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '新增古树信息'
  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = key === 'ageEstimateType' || key === 'healthStatus' || key === 'protectionLevel' ? null : ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (id) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑古树信息'
  try {
    const res = await api.getAncientTreeById(id)
    if (res.code === 200) {
      const data = res.data
      // 填充表单
      Object.keys(formData).forEach(key => {
        formData[key] = data[key] !== undefined ? data[key] : (
          key === 'ageEstimateType' || key === 'healthStatus' || key === 'protectionLevel' ? null : ''
        )
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，获取详情失败')
  }
}

// 查看详情
const handleView = async (id) => {
  try {
    const res = await api.getAncientTreeById(id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，获取详情失败')
  }
}

// 删除
const handleDelete = async (id) => {
  ElMessageBox.confirm(
    '确定要删除这条古树信息吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await api.deleteAncientTree(id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getAncientTreePage()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('网络错误，删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 表单提交
const handleSubmit = async () => {
  // 表单验证
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  try {
    let res
    if (dialogType.value === 'add') {
      res = await api.createAncientTree(formData)
    } else {
      res = await api.updateAncientTree(formData.id, formData)
    }

    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
      getAncientTreePage()
    } else {
      ElMessage.error(res.msg || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
    }
  } catch (error) {
    ElMessage.error('网络错误，操作失败')
  }
}

// 获取保护级别标签类型
const getLevelType = (level) => {
  switch (level) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'success'
    default: return 'info'
  }
}

// 权限检查（实际项目中实现）
const checkPermissions = () => {
  // 示例：从全局状态获取用户角色
  // const roles = store.state.user.roles
  // hasCreatePermission.value = roles.includes('ADMIN') || roles.includes('MANAGER')
  // hasUpdatePermission.value = roles.includes('ADMIN') || roles.includes('MANAGER')
  // hasDeletePermission.value = roles.includes('ADMIN')
}
</script>

<style scoped>
.ancient-tree-management {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 60px);
}

.mb-4 {
  margin-bottom: 16px !important;
}
</style>