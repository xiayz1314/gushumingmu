<template>
  <div class="tree-map-optimized">
    <!-- 主地图区域 -->
    <div class="map-section">
      <!-- 地图容器 -->
      <div 
        id="mapContainer" 
        class="map-container"
        v-loading="initialLoading"
        element-loading-text="正在初始化地图..."
        element-loading-background="rgba(255, 255, 255, 0.95)"
      >
        <!-- 地图遮罩层 -->
        <div v-if="dataLoading" class="map-loading-mask">
          <div class="loading-spinner">
            <div class="spinner"></div>
            <span class="loading-text">加载古树数据中...</span>
            <span class="loading-progress">{{ loadingProgress }}%</span>
          </div>
        </div>
      </div>

      <!-- 地图控件 -->
      <div class="map-controls">
        <!-- 定位控件 -->
        <div class="locate-control" @click="locateToDefault">
          <el-tooltip content="定位到中国" placement="left">
            <el-button circle type="primary" size="large">
              <el-icon><Position /></el-icon>
            </el-button>
          </el-tooltip>
        </div>

        <!-- 缩放控件 -->
        <div class="zoom-control">
          <el-button 
            circle 
            size="small" 
            @click="zoomIn"
            :disabled="currentZoom >= maxZoom"
          >
            <el-icon><Plus /></el-icon>
          </el-button>
          <div class="zoom-display">{{ currentZoom }}级</div>
          <el-button 
            circle 
            size="small" 
            @click="zoomOut"
            :disabled="currentZoom <= minZoom"
          >
            <el-icon><Minus /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 图层控制 -->
      <div class="layer-control">
        <el-tooltip content="切换地图样式" placement="left">
          <el-dropdown @command="changeMapStyle" trigger="click">
            <el-button circle size="large" type="info">
              <el-icon><Operation /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="normal">标准地图</el-dropdown-item>
                <el-dropdown-item command="light">清新风格</el-dropdown-item>
                <el-dropdown-item command="dark">深色风格</el-dropdown-item>
                <el-dropdown-item command="satellite">卫星影像</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-tooltip>
      </div>
    </div>

    <!-- 侧边控制面板 -->
    <div class="sidebar-panel" :class="{ 'collapsed': sidebarCollapsed }">
      <!-- 面板头部 -->
      <div class="sidebar-header">
        <div class="header-content">
          <h2 class="panel-title">
            <el-icon><MapLocation /></el-icon>
            <span v-if="!sidebarCollapsed">古树分布可视化系统</span>
          </h2>
          <div class="header-actions">
            <el-button 
              circle 
              size="small" 
              @click="sidebarCollapsed = !sidebarCollapsed"
            >
              <el-icon :size="16">{{ sidebarCollapsed ? ArrowRight : ArrowLeft }}</el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 面板内容 -->
      <div class="sidebar-content" v-show="!sidebarCollapsed">
        <!-- 系统状态 -->
        <div class="system-status">
          <div class="status-item">
            <div class="status-label">系统状态</div>
            <div class="status-value">
              <el-tag :type="systemOnline ? 'success' : 'danger'" size="small">
                {{ systemOnline ? '在线' : '离线' }}
              </el-tag>
            </div>
          </div>
          <div class="status-item">
            <div class="status-label">数据更新</div>
            <div class="status-value">{{ lastUpdateTime }}</div>
          </div>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-section">
          <h3 class="section-title">
            <el-icon><Filter /></el-icon>
            数据筛选
          </h3>
          
          <!-- 保护等级筛选 -->
          <div class="filter-group">
            <label class="filter-label">保护等级</label>
            <el-select 
              v-model="filterForm.protectionLevel" 
              placeholder="全部等级" 
              size="small" 
              @change="handleFilterChange" 
              clearable
              class="filter-input"
            >
              <el-option 
                v-for="level in protectionLevels" 
                :key="level.value"
                :value="level.value"
                :label="level.label"
              >
                <span class="level-option">
                  <span class="level-dot" :class="`level-${level.value}`"></span>
                  {{ level.label }}
                </span>
              </el-option>
            </el-select>
          </div>

          <!-- 健康状况筛选 -->
          <div class="filter-group">
            <label class="filter-label">健康状况</label>
            <el-select 
              v-model="filterForm.healthStatus" 
              placeholder="全部状态" 
              size="small" 
              @change="handleFilterChange" 
              clearable
              class="filter-input"
            >
              <el-option 
                v-for="status in healthStatuses" 
                :key="status.value"
                :value="status.value"
                :label="status.label"
              >
                <span class="status-option">
                  <span class="status-dot" :class="`status-${status.value}`"></span>
                  {{ status.label }}
                </span>
              </el-option>
            </el-select>
          </div>

          <!-- 树种筛选 -->
          <div class="filter-group">
            <label class="filter-label">树种查询</label>
            <el-autocomplete
              v-model="filterForm.species"
              :fetch-suggestions="searchSpecies"
              placeholder="输入树种名称"
              @select="handleFilterChange"
              clearable
              class="filter-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-autocomplete>
          </div>

          <!-- 树龄范围 -->
          <div class="filter-group">
            <label class="filter-label">树龄范围</label>
            <div class="age-range">
              <el-input-number 
                v-model="filterForm.minAge"
                :min="0"
                :max="5000"
                placeholder="最小树龄"
                size="small"
                @change="handleFilterChange"
                controls-position="right"
                class="age-input"
              />
              <span class="range-separator">-</span>
              <el-input-number 
                v-model="filterForm.maxAge"
                :min="0"
                :max="5000"
                placeholder="最大树龄"
                size="small"
                @change="handleFilterChange"
                controls-position="right"
                class="age-input"
              />
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="filter-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="loadMapData" 
              :loading="dataLoading"
              :icon="Refresh"
              class="action-btn"
            >
              查询数据
            </el-button>
            <el-button 
              type="default" 
              size="small" 
              @click="resetFilters"
              :icon="RefreshLeft"
              class="action-btn"
            >
              重置筛选
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="exportData"
              :icon="Download"
              class="action-btn"
            >
              导出数据
            </el-button>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="statistics-section">
          <h3 class="section-title">
            <el-icon><DataAnalysis /></el-icon>
            数据统计
          </h3>
          
          <!-- 总览统计 -->
          <div class="stat-overview">
            <div class="stat-card">
              <div class="stat-icon total">
                <el-icon><Histogram /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalCount.toLocaleString() }}</div>
                <div class="stat-label">古树总数</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon age">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ avgAge.toFixed(0) }}年</div>
                <div class="stat-label">平均树龄</div>
              </div>
            </div>
          </div>

          <!-- 分类统计 -->
          <div class="stat-details">
            <div class="stat-row">
              <span class="stat-title">保护等级分布</span>
              <div class="stat-bars">
                <div class="stat-bar level-1" :style="{ width: level1Percent + '%' }">
                  <span class="bar-label">一级 {{ level1Count }}</span>
                </div>
                <div class="stat-bar level-2" :style="{ width: level2Percent + '%' }">
                  <span class="bar-label">二级 {{ level2Count }}</span>
                </div>
                <div class="stat-bar level-3" :style="{ width: level3Percent + '%' }">
                  <span class="bar-label">三级 {{ level3Count }}</span>
                </div>
              </div>
            </div>

            <div class="stat-row">
              <span class="stat-title">健康状况分布</span>
              <div class="stat-bars">
                <div class="stat-bar status-1" :style="{ width: healthyPercent + '%' }">
                  <span class="bar-label">健康 {{ healthyCount }}</span>
                </div>
                <div class="stat-bar status-2" :style="{ width: normalPercent + '%' }">
                  <span class="bar-label">一般 {{ normalCount }}</span>
                </div>
                <div class="stat-bar status-3" :style="{ width: dangerPercent + '%' }">
                  <span class="bar-label">濒危 {{ dangerCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 数据操作 -->
        <div class="action-section">
          <h3 class="section-title">
            <el-icon><Setting /></el-icon>
            数据操作
          </h3>
          
          <div class="action-grid">
            <el-tooltip content="查看数据详情" placement="top">
              <el-button 
                type="primary" 
                circle 
                @click="showDataTable"
                size="large"
              >
                <el-icon><DataBoard /></el-icon>
              </el-button>
            </el-tooltip>
            
            <el-tooltip content="清除地图标记" placement="top">
              <el-button 
                type="warning" 
                circle 
                @click="clearMarkers"
                size="large"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
            
            <el-tooltip content="保存当前视图" placement="top">
              <el-button 
                type="success" 
                circle 
                @click="saveView"
                size="large"
              >
                <el-icon><DocumentAdd /></el-icon>
              </el-button>
            </el-tooltip>
            
            <el-tooltip content="系统设置" placement="top">
              <el-button 
                type="info" 
                circle 
                @click="showSettings"
                size="large"
              >
                <el-icon><Tools /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部信息栏 -->
    <div class="footer-bar">
      <div class="footer-content">
        <div class="coordinate-display">
          <span class="coord-label">经度:</span>
          <span class="coord-value">{{ currentCoords.lng.toFixed(6) }}</span>
          <span class="coord-label">纬度:</span>
          <span class="coord-value">{{ currentCoords.lat.toFixed(6) }}</span>
        </div>
        
        <div class="zoom-display">
          <span class="zoom-label">缩放级别:</span>
          <span class="zoom-value">{{ currentZoom }}</span>
        </div>
        
        <div class="data-info">
          <span class="data-label">当前显示:</span>
          <span class="data-value">{{ markers.length }} 个标记点</span>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialog.visible"
      :title="detailDialog.title"
      width="500px"
      destroy-on-close
    >
      <div v-if="detailDialog.data" class="tree-detail">
        <div class="detail-header">
          <h3>{{ detailDialog.data.treeName }}</h3>
          <div class="detail-tags">
            <el-tag :type="getLevelTagType(detailDialog.data.protectionLevel)" size="small">
              {{ getProtectionLevelText(detailDialog.data.protectionLevel) }}
            </el-tag>
            <el-tag :type="getStatusTagType(detailDialog.data.healthStatus)" size="small">
              {{ getHealthStatusText(detailDialog.data.healthStatus) }}
            </el-tag>
          </div>
        </div>
        
        <div class="detail-grid">
          <div class="detail-item">
            <label>树种:</label>
            <span>{{ detailDialog.data.species || '未知' }}</span>
          </div>
          <div class="detail-item">
            <label>树龄:</label>
            <span>{{ detailDialog.data.age || '未知' }} 年</span>
          </div>
          <div class="detail-item">
            <label>胸径:</label>
            <span>{{ detailDialog.data.diameter || '未知' }} cm</span>
          </div>
          <div class="detail-item">
            <label>高度:</label>
            <span>{{ detailDialog.data.height || '未知' }} 米</span>
          </div>
          <div class="detail-item">
            <label>冠幅:</label>
            <span>{{ detailDialog.data.crownWidth || '未知' }} 米</span>
          </div>
          <div class="detail-item">
            <label>坐标:</label>
            <span>{{ detailDialog.data.longitude }}, {{ detailDialog.data.latitude }}</span>
          </div>
          <div class="detail-item full-width">
            <label>详细位置:</label>
            <span>{{ detailDialog.data.locationDetail || '未知位置' }}</span>
          </div>
          <div class="detail-item full-width">
            <label>保护措施:</label>
            <span>{{ detailDialog.data.protectionMeasures || '暂无' }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, onMounted, reactive, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  RefreshLeft,
  Search,
  Position,
  Plus,
  Minus,
  Operation,
  MapLocation,
  ArrowRight,
  ArrowLeft,
  Filter,
  Download,
  DataAnalysis,
  Histogram,
  Timer,
  Setting,
  DataBoard,
  Delete,
  DocumentAdd,
  Tools
} from '@element-plus/icons-vue'
import api from '@/api/index.js'
import { debounce } from 'lodash-es'

// 环境变量获取高德地图Key
const amapKey = import.meta.env.VITE_AMAP_KEY || '37ee9d4426b015a2f20772159abd156e'

// 核心响应式数据
const map = shallowRef<any>(null)
const markers = ref<any[]>([])
const initialLoading = ref<boolean>(false)
const dataLoading = ref<boolean>(false)
const loadingProgress = ref<number>(0)

// 侧边栏状态
const sidebarCollapsed = ref<boolean>(false)

// 地图状态
const currentZoom = ref<number>(5)
const currentCoords = reactive({
  lng: 104.195397,
  lat: 35.86166
})
const minZoom = ref<number>(3)
const maxZoom = ref<number>(18)

// 系统状态
const systemOnline = ref<boolean>(true)
const lastUpdateTime = ref<string>('刚刚')

// 筛选表单
const filterForm = reactive({
  protectionLevel: '',
  healthStatus: '',
  species: '',
  minAge: undefined as number | undefined,
  maxAge: undefined as number | undefined,
  sortField: 'id',
  sortOrder: 'ASC'
})

// 详情弹窗
const detailDialog = reactive({
  visible: false,
  title: '',
  data: null as any
})

// 数据统计
const totalCount = ref<number>(0)
const level1Count = ref<number>(0)
const level2Count = ref<number>(0)
const level3Count = ref<number>(0)
const healthyCount = ref<number>(0)
const normalCount = ref<number>(0)
const dangerCount = ref<number>(0)
const avgAge = ref<number>(0)

// 地图范围
const currentBounds = reactive({
  minLng: 73.66,
  maxLng: 135.05,
  minLat: 3.86,
  maxLat: 53.55
})

// 枚举选项
const protectionLevels = [
  { value: '1', label: '一级保护' },
  { value: '2', label: '二级保护' },
  { value: '3', label: '三级保护' }
]

const healthStatuses = [
  { value: '1', label: '健康' },
  { value: '2', label: '一般' },
  { value: '3', label: '濒危' }
]

// 计算属性
const level1Percent = computed(() => {
  return totalCount.value ? Math.round((level1Count.value / totalCount.value) * 100) : 0
})

const level2Percent = computed(() => {
  return totalCount.value ? Math.round((level2Count.value / totalCount.value) * 100) : 0
})

const level3Percent = computed(() => {
  return totalCount.value ? Math.round((level3Count.value / totalCount.value) * 100) : 0
})

const healthyPercent = computed(() => {
  return totalCount.value ? Math.round((healthyCount.value / totalCount.value) * 100) : 0
})

const normalPercent = computed(() => {
  return totalCount.value ? Math.round((normalCount.value / totalCount.value) * 100) : 0
})

const dangerPercent = computed(() => {
  return totalCount.value ? Math.round((dangerCount.value / totalCount.value) * 100) : 0
})

// 加载防抖函数
let debouncedLoadData: Function
let abortController: AbortController | null = null

// 标记点缓存
const markerCache = new Map<string, any>()

/**
 * 初始化防抖函数
 */
const initDebouncedFunctions = () => {
  debouncedLoadData = debounce(() => {
    loadMapData()
  }, 800)
}

/**
 * 验证经纬度合法性
 */
const isValidCoord = (lon: number, lat: number): boolean => {
  if (typeof lon !== 'number' || typeof lat !== 'number') return false
  if (isNaN(lon) || isNaN(lat)) return false
  if (lon < -180 || lon > 180 || lat < -90 || lat > 90) return false
  return true
}

/**
 * 构建查询参数 - 修复：移除size限制，使用分页分批加载
 */
const buildQueryParams = () => {
  const params: any = {
    minLng: currentBounds.minLng,
    maxLng: currentBounds.maxLng,
    minLat: currentBounds.minLat,
    maxLat: currentBounds.maxLat,
    page: 0,
    size: 10000, // 增加一次性加载数量
    sortField: filterForm.sortField,
    sortOrder: filterForm.sortOrder
  }

  // 添加筛选条件
  if (filterForm.protectionLevel) {
    params.protectionLevel = filterForm.protectionLevel
  }
  if (filterForm.healthStatus) {
    params.healthStatus = filterForm.healthStatus
  }
  if (filterForm.species && filterForm.species.trim()) {
    params.species = filterForm.species.trim()
  }
  if (filterForm.minAge !== undefined) {
    params.minAge = filterForm.minAge
  }
  if (filterForm.maxAge !== undefined) {
    params.maxAge = filterForm.maxAge
  }

  return params
}

/**
 * 批量加载地图数据 - 修复：支持大量数据分页加载
 */
const loadMapData = async (): Promise<void> => {
  // 取消之前的请求
  if (abortController) {
    abortController.abort()
  }
  
  // 创建新的AbortController
  abortController = new AbortController()
  
  dataLoading.value = true
  loadingProgress.value = 0
  
  try {
    const params = buildQueryParams()
    
    // 模拟进度更新
    const progressInterval = setInterval(() => {
      if (loadingProgress.value < 90) {
        loadingProgress.value += 10
      }
    }, 200)
    
    console.log('请求参数:', params)
    
    // 调用API - 注意：这里可能需要后端支持分页
    const res = await api.stats.getMapData(params, {
      signal: abortController.signal
    })
    
    clearInterval(progressInterval)
    loadingProgress.value = 100
    
    if (res?.code === 200) {
      const data = res.data
      console.log('API响应:', {
        total: data.total,
        recordsLength: data.records?.length,
        hasMore: data.hasMore
      })
      
      // 更新统计信息
      updateStatistics(data)
      
      // 渲染标记点
      if (data.records && data.records.length > 0) {
        renderTreeMarkers(data.records)
        
        // 如果有更多数据，继续加载下一页
        if (data.hasMore) {
          await loadMoreData(params.page + 1, data.records)
        }
      } else {
        clearMarkers()
      }
      
      // 更新最后更新时间
      lastUpdateTime.value = new Date().toLocaleTimeString()
      
      if (data.records && data.records.length > 0) {
        ElMessage.success(`成功加载 ${markers.value.length} 个标记点`)
      } else {
        ElMessage.warning('当前范围内未找到符合条件的古树')
      }
    } else {
      ElMessage.error(res?.msg || '数据加载失败')
    }
  } catch (error: any) {
    // 如果是取消请求，不显示错误
    if (error.name === 'AbortError') {
      console.log('请求已取消')
      return
    }
    
    console.error('数据加载错误:', error)
    systemOnline.value = false
    ElMessage.error('网络连接失败，请检查网络设置')
  } finally {
    dataLoading.value = false
    loadingProgress.value = 0
    setTimeout(() => {
      systemOnline.value = true
    }, 5000)
  }
}

/**
 * 加载更多数据
 */
const loadMoreData = async (page: number, existingRecords: any[]) => {
  try {
    const params = buildQueryParams()
    params.page = page
    
    const res = await api.stats.getMapData(params)
    
    if (res?.code === 200 && res.data.records && res.data.records.length > 0) {
      // 合并数据
      const allRecords = [...existingRecords, ...res.data.records]
      
      // 更新标记点
      renderTreeMarkers(allRecords)
      
      // 继续加载下一页
      if (res.data.hasMore) {
        await loadMoreData(page + 1, allRecords)
      }
    }
  } catch (error) {
    console.error('加载更多数据失败:', error)
  }
}

/**
 * 更新统计信息
 */
const updateStatistics = (data: any) => {
  totalCount.value = data.total || 0
  level1Count.value = data.level1Count || 0
  level2Count.value = data.level2Count || 0
  level3Count.value = data.level3Count || 0
  healthyCount.value = data.healthyCount || 0
  normalCount.value = data.normalCount || 0
  dangerCount.value = data.dangerCount || 0
  avgAge.value = data.avgAge || 0
}

/**
 * 渲染树标记点 - 修复：确保标记正确显示
 */
const renderTreeMarkers = (trees: any[]): void => {
  if (!map.value) {
    console.warn('地图未初始化')
    return
  }
  
  const AMap = (window as any).AMap
  if (!AMap) {
    console.error('AMap未加载')
    return
  }

  console.log(`开始渲染 ${trees.length} 个标记点`)

  // 清除现有标记
  clearMarkers()

  // 创建新标记
  const newMarkers: any[] = []

  trees.forEach((tree, index) => {
    const lon = parseFloat(tree.longitude)
    const lat = parseFloat(tree.latitude)
    
    if (!isValidCoord(lon, lat)) {
      console.warn(`坐标无效: ${tree.id}`, lon, lat)
      return
    }

    // 根据保护等级选择颜色
    const color = getMarkerColor(tree.protectionLevel)
    
    // 创建标记内容
    const markerContent = document.createElement('div')
    markerContent.className = 'custom-marker'
    markerContent.innerHTML = `
      <div class="marker-pin" style="background-color: ${color}"></div>
      <div class="marker-pulse" style="background-color: ${color}"></div>
    `

    // 创建标记
    const marker = new AMap.Marker({
      position: [lon, lat],
      content: markerContent,
      offset: new AMap.Pixel(-10, -10),
      title: tree.treeName || '古树',
      extData: tree
    })

    // 绑定点击事件
    marker.on('click', () => {
      console.log('标记被点击:', tree)
      showTreeDetail(tree)
    })

    newMarkers.push(marker)
  })

  // 批量添加标记到地图
  if (newMarkers.length > 0) {
    map.value.add(newMarkers)
    markers.value = newMarkers
    
    // 如果标记太多，使用聚合
    if (newMarkers.length > 1000) {
      createMarkerCluster(newMarkers)
    }
    
    console.log(`成功添加 ${newMarkers.length} 个标记点`)
    
    // 调整地图视野
    if (newMarkers.length > 0) {
      setTimeout(() => {
        try {
          map.value?.setFitView(newMarkers, false, [50, 50, 50, 50], 12)
        } catch (error) {
          console.warn('调整视野失败:', error)
        }
      }, 300)
    }
  }
}

/**
 * 创建标记聚合
 */
const createMarkerCluster = (markerList: any[]) => {
  const AMap = (window as any).AMap
  if (!AMap || !AMap.MarkerClusterer) {
    console.warn('MarkerClusterer未加载')
    return
  }

  try {
    // 创建聚合器
    const cluster = new AMap.MarkerClusterer(map.value, markerList, {
      gridSize: 80,
      maxZoom: 15,
      minClusterSize: 2,
      renderClusterMarker: (context: any) => {
        const count = context.count
        const div = document.createElement('div')
        
        // 根据数量设置不同样式
        let size = 30
        let fontSize = 12
        let bgColor = '#1890ff'
        
        if (count > 100) {
          size = 40
          fontSize = 14
          bgColor = '#ff4d4f'
        } else if (count > 10) {
          size = 35
          fontSize = 13
          bgColor = '#faad14'
        }
        
        div.innerHTML = `
          <div style="
            width: ${size}px;
            height: ${size}px;
            background-color: ${bgColor};
            border: 3px solid white;
            border-radius: 50%;
            color: white;
            font-size: ${fontSize}px;
            font-weight: bold;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            cursor: pointer;
          ">${count}</div>
        `
        
        div.onclick = () => {
          map.value.setZoomAndCenter(context.zoom + 2, context.center)
        }
        
        return div
      }
    })
    
    // 保存聚合器引用
    markers.value.push(cluster)
  } catch (error) {
    console.error('创建聚合失败:', error)
  }
}

/**
 * 获取标记颜色
 */
const getMarkerColor = (level: number): string => {
  switch (level) {
    case 1: return '#ff4d4f' // 红色
    case 2: return '#faad14' // 黄色
    case 3: return '#1890ff' // 蓝色
    default: return '#52c41a' // 绿色
  }
}

/**
 * 清除所有标记
 */
const clearMarkers = (): void => {
  if (!map.value || markers.value.length === 0) return
  
  console.log('清除标记点')
  
  markers.value.forEach((marker) => {
    try {
      if (marker.clearMarkers) {
        // 如果是聚合器
        marker.clearMarkers()
      } else {
        // 如果是普通标记
        map.value?.remove(marker)
      }
    } catch (e) {
      console.warn('清除标记失败:', e)
    }
  })
  
  markers.value = []
  markerCache.clear()
}

/**
 * 显示树详情
 */
const showTreeDetail = (tree: any): void => {
  detailDialog.visible = true
  detailDialog.title = tree.treeName || '古树详情'
  detailDialog.data = tree
}

/**
 * 获取保护等级文本
 */
const getProtectionLevelText = (level: number): string => {
  switch (level) {
    case 1: return '一级保护'
    case 2: return '二级保护'
    case 3: return '三级保护'
    default: return '未分级'
  }
}

/**
 * 获取健康状态文本
 */
const getHealthStatusText = (status: number): string => {
  switch (status) {
    case 1: return '健康'
    case 2: return '一般'
    case 3: return '濒危'
    default: return '未知'
  }
}

/**
 * 获取保护等级标签类型
 */
const getLevelTagType = (level: number): string => {
  switch (level) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'primary'
    default: return 'info'
  }
}

/**
 * 获取健康状态标签类型
 */
const getStatusTagType = (status: number): string => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

/**
 * 树种搜索
 */
const searchSpecies = (query: string, cb: Function) => {
  // 这里应该调用API获取树种建议，这里模拟数据
  const suggestions = [
    { value: '松树' },
    { value: '柏树' },
    { value: '银杏' },
    { value: '榕树' },
    { value: '槐树' },
    { value: '枫树' },
    { value: '樟树' },
    { value: '楠木' }
  ]
  
  const results = query 
    ? suggestions.filter(item => 
        item.value.toLowerCase().includes(query.toLowerCase())
      )
    : suggestions.slice(0, 5)
  
  cb(results)
}

/**
 * 筛选条件变化处理
 */
const handleFilterChange = () => {
  debouncedLoadData()
}

/**
 * 重置所有筛选条件
 */
const resetFilters = () => {
  filterForm.protectionLevel = ''
  filterForm.healthStatus = ''
  filterForm.species = ''
  filterForm.minAge = undefined
  filterForm.maxAge = undefined
  filterForm.sortField = 'id'
  filterForm.sortOrder = 'ASC'
  
  // 重置地图范围到默认值
  currentBounds.minLng = 73.66
  currentBounds.maxLng = 135.05
  currentBounds.minLat = 3.86
  currentBounds.maxLat = 53.55
  
  loadMapData()
}

/**
 * 设置地图范围监听
 */
const setupMapBoundsListener = () => {
  if (!map.value) return
  
  // 地图移动防抖
  const moveEndDebounced = debounce(() => {
    try {
      const bounds = map.value.getBounds()
      if (bounds) {
        const sw = bounds.getSouthWest()
        const ne = bounds.getNorthEast()
        
        if (sw && ne) {
          const newBounds = {
            minLng: sw.lng,
            maxLng: ne.lng,
            minLat: sw.lat,
            maxLat: ne.lat
          }
          
          // 检查范围是否有显著变化
          if (shouldUpdateBounds(newBounds)) {
            // 更新当前范围
            Object.assign(currentBounds, newBounds)
            
            // 使用防抖函数加载数据
            debouncedLoadData()
          }
        }
      }
    } catch (error) {
      console.warn('处理地图范围变化失败:', error)
    }
  }, 1000)
  
  map.value.on('moveend', moveEndDebounced)
  map.value.on('zoomchange', moveEndDebounced)
  
  // 更新当前坐标和缩放级别
  map.value.on('mousemove', (e: any) => {
    currentCoords.lng = e.lnglat.getLng()
    currentCoords.lat = e.lnglat.getLat()
  })
  
  map.value.on('zoomend', () => {
    currentZoom.value = map.value.getZoom()
  })
}

/**
 * 判断是否需要更新范围
 */
const shouldUpdateBounds = (newBounds: typeof currentBounds): boolean => {
  // 如果范围变化超过0.1度，则更新
  const threshold = 0.1
  return (
    Math.abs(newBounds.minLng - currentBounds.minLng) > threshold ||
    Math.abs(newBounds.maxLng - currentBounds.maxLng) > threshold ||
    Math.abs(newBounds.minLat - currentBounds.minLat) > threshold ||
    Math.abs(newBounds.maxLat - currentBounds.maxLat) > threshold
  )
}

/**
 * 加载高德地图脚本
 */
const loadAmapScript = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if ((window as any).AMap) {
      return resolve()
    }

    const scriptId = 'amap-script'
    let script = document.getElementById(scriptId) as HTMLScriptElement | null

    if (script) {
      const checkLoaded = () => {
        if ((window as any).AMap) {
          resolve()
        } else {
          setTimeout(checkLoaded, 100)
        }
      }
      checkLoaded()
      return
    }

    script = document.createElement('script')
    script.id = scriptId
    script.type = 'text/javascript'
    script.charset = 'utf-8'
    script.crossOrigin = 'anonymous'
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}&plugin=AMap.ToolBar,AMap.MarkerCluster`

    const timeout = setTimeout(() => {
      script?.remove()
      reject(new Error('地图脚本加载超时'))
    }, 15000)

    script.onload = () => {
      clearTimeout(timeout)
      if ((window as any).AMap) {
        resolve()
      } else {
        reject(new Error('地图脚本加载失败'))
      }
    }

    script.onerror = () => {
      clearTimeout(timeout)
      reject(new Error('地图脚本加载失败'))
    }

    document.head.appendChild(script)
  })
}

/**
 * 初始化地图
 */
const initMap = async () => {
  const container = document.getElementById('mapContainer')
  
  if (!container) {
    ElMessage.error('地图容器未找到')
    return
  }

  const AMap = (window as any).AMap
  if (!AMap) {
    ElMessage.error('地图加载失败')
    return
  }

  try {
    // 初始化地图
    map.value = new AMap.Map(container, {
      zoom: currentZoom.value,
      center: [currentCoords.lng, currentCoords.lat],
      viewMode: '2D',
      mapStyle: 'amap://styles/light',
      resizeEnable: true,
      zoomEnable: true,
      dragEnable: true,
      doubleClickZoom: false,
      animateEnable: true,
      minZoom: minZoom.value,
      maxZoom: maxZoom.value
    })

    // 添加工具条
    const toolbar = new AMap.ToolBar({
      position: 'RB',
      locate: false
    })
    map.value.addControl(toolbar)

    // 地图加载完成
    map.value.on('complete', () => {
      console.log('地图加载完成')
      
      // 获取初始范围
      const bounds = map.value.getBounds()
      if (bounds) {
        const sw = bounds.getSouthWest()
        const ne = bounds.getNorthEast()
        
        currentBounds.minLng = sw.lng
        currentBounds.maxLng = ne.lng
        currentBounds.minLat = sw.lat
        currentBounds.maxLat = ne.lat
        
        console.log('初始地图范围:', currentBounds)
      }
      
      // 设置范围监听
      setupMapBoundsListener()
      
      // 加载初始数据
      setTimeout(() => {
        loadMapData()
      }, 500)
    })
  } catch (error: any) {
    console.error('地图初始化失败:', error)
    ElMessage.error(`地图初始化失败：${error.message}`)
  }
}

/**
 * 缩放控制
 */
const zoomIn = () => {
  if (map.value && currentZoom.value < maxZoom.value) {
    map.value.setZoom(currentZoom.value + 1)
  }
}

const zoomOut = () => {
  if (map.value && currentZoom.value > minZoom.value) {
    map.value.setZoom(currentZoom.value - 1)
  }
}

/**
 * 定位到默认位置
 */
const locateToDefault = () => {
  if (map.value) {
    map.value.setZoomAndCenter(5, [104.195397, 35.86166])
  }
}

/**
 * 切换地图样式
 */
const changeMapStyle = (style: string) => {
  if (!map.value) return
  
  const styles: Record<string, string> = {
    normal: 'amap://styles/normal',
    light: 'amap://styles/light',
    dark: 'amap://styles/dark',
    satellite: 'amap://styles/satellite'
  }
  
  map.value.setMapStyle(styles[style] || styles.light)
}

/**
 * 导出数据
 */
const exportData = async () => {
  try {
    const params = buildQueryParams()
    const res = await api.stats.getMapData({ ...params, size: 10000 })
    
    if (res?.code === 200) {
      const data = res.data.records || []
      const csvContent = convertToCSV(data)
      downloadCSV(csvContent, `古树数据_${new Date().getTime()}.csv`)
      ElMessage.success('数据导出成功')
    }
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('数据导出失败')
  }
}

/**
 * 转换为CSV格式
 */
const convertToCSV = (data: any[]): string => {
  if (data.length === 0) return ''
  
  const headers = Object.keys(data[0])
  const rows = data.map(item => 
    headers.map(header => `"${item[header] || ''}"`).join(',')
  )
  
  return [headers.join(','), ...rows].join('\n')
}

/**
 * 下载CSV文件
 */
const downloadCSV = (content: string, filename: string) => {
  const blob = new Blob(['\ufeff' + content], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 显示数据表格
 */
const showDataTable = () => {
  ElMessage.info('数据表格功能开发中...')
}

/**
 * 保存当前视图
 */
const saveView = () => {
  if (!map.value) return
  
  const viewState = {
    center: map.value.getCenter(),
    zoom: map.value.getZoom(),
    bounds: map.value.getBounds(),
    timestamp: new Date().getTime()
  }
  
  localStorage.setItem('mapViewState', JSON.stringify(viewState))
  ElMessage.success('当前视图已保存')
}

/**
 * 显示系统设置
 */
const showSettings = () => {
  ElMessage.info('系统设置功能开发中...')
}

/**
 * 处理窗口大小变化
 */
const handleResize = () => {
  if (map.value) {
    setTimeout(() => {
      map.value.resize()
    }, 100)
  }
}

// 初始化防抖函数
initDebouncedFunctions()

// 生命周期钩子
onMounted(async () => {
  initialLoading.value = true
  
  try {
    await loadAmapScript()
    await initMap()
    window.addEventListener('resize', handleResize)
  } catch (error: any) {
    ElMessage.error(error.message || '初始化失败')
  } finally {
    initialLoading.value = false
  }
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.tree-map-optimized {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
}

.map-section {
  flex: 1;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.map-loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 3px solid #e6e6e6;
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

.loading-text {
  display: block;
  margin-bottom: 10px;
  color: #606266;
  font-size: 16px;
}

.loading-progress {
  display: block;
  color: #409eff;
  font-size: 14px;
  font-weight: bold;
}

.map-controls {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 100;
}

.locate-control {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.layer-control {
  position: absolute;
  top: 80px;
  right: 20px;
  z-index: 100;
}

.zoom-control {
  position: absolute;
  bottom: 100px;
  right: 20px;
  background: white;
  border-radius: 25px;
  padding: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  z-index: 100;
}

.zoom-display {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.sidebar-panel {
  width: 380px;
  height: 100%;
  background: white;
  box-shadow: -2px 0 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  z-index: 200;
  
  &.collapsed {
    width: 60px;
    
    .sidebar-content {
      opacity: 0;
      visibility: hidden;
      pointer-events: none;
    }
    
    .sidebar-header {
      padding: 20px 0;
      
      .panel-title {
        justify-content: center;
      }
    }
  }
}

.sidebar-header {
  padding: 20px;
  background: linear-gradient(135deg, #409eff 0%, #1890ff 100%);
  color: white;
  flex-shrink: 0;
  min-height: 76px;
  box-sizing: border-box;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  
  span {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.system-status {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #e9ecef;
  margin: 0;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.status-label {
  color: #666;
  font-size: 13px;
  min-width: 60px;
}

.status-value {
  color: #333;
  font-size: 13px;
  font-weight: 500;
}

.section-title {
  margin: 0 0 15px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin: 0;
}

.filter-group {
  margin-bottom: 15px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.filter-label {
  display: block;
  margin-bottom: 6px;
  color: #666;
  font-size: 13px;
  font-weight: 500;
}

.filter-input {
  width: 100%;
}

.age-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.age-input {
  flex: 1;
  width: 0;
}

.range-separator {
  color: #999;
  padding: 0 5px;
  flex-shrink: 0;
}

.level-option,
.status-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-dot,
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.level-1 { background: #ff4d4f; }
.level-2 { background: #faad14; }
.level-3 { background: #1890ff; }

.status-1 { background: #52c41a; }
.status-2 { background: #faad14; }
.status-3 { background: #ff4d4f; }

.filter-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.action-btn {
  flex: 1;
}

.statistics-section {
  margin: 0;
}

.stat-overview {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  min-width: 0;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
  
  &.total {
    background: linear-gradient(135deg, #1890ff 0%, #409eff 100%);
  }
  
  &.age {
    background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  }
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-details {
  background: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.stat-row {
  margin-bottom: 15px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.stat-title {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.stat-bars {
  display: flex;
  height: 24px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.stat-bar {
  position: relative;
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  
  &.level-1 { background: #ff4d4f; }
  &.level-2 { background: #faad14; }
  &.level-3 { background: #1890ff; }
  
  &.status-1 { background: #52c41a; }
  &.status-2 { background: #faad14; }
  &.status-3 { background: #ff4d4f; }
  
  .bar-label {
    color: white;
    font-size: 11px;
    font-weight: 600;
    white-space: nowrap;
    padding: 0 5px;
    text-align: center;
  }
}

.action-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  margin: 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.footer-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid #f0f0f0;
  padding: 8px 20px;
  z-index: 100;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  flex-wrap: wrap;
  gap: 10px;
}

.coordinate-display {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.coord-label {
  font-weight: 500;
}

.coord-value {
  font-family: 'Courier New', monospace;
  color: #333;
}

.zoom-display,
.data-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.tree-detail {
  .detail-header {
    margin-bottom: 20px;
    
    h3 {
      margin: 0 0 10px 0;
      color: #333;
      font-size: 18px;
    }
  }
  
  .detail-tags {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .detail-item {
    display: flex;
    flex-direction: column;
    
    label {
      font-size: 12px;
      color: #999;
      margin-bottom: 4px;
    }
    
    span {
      font-size: 14px;
      color: #333;
      font-weight: 500;
    }
    
    &.full-width {
      grid-column: 1 / -1;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.2); opacity: 0.4; }
  100% { transform: scale(1); opacity: 0.7; }
}

/* 自定义标记样式 */
.custom-marker {
  position: relative;
  width: 20px;
  height: 20px;
}

.marker-pin {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  position: relative;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
}

.marker-pulse {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  animation: pulse 2s infinite;
  z-index: 1;
  opacity: 0.7;
}

/* 滚动条样式 */
.sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.sidebar-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.sidebar-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar-panel {
    width: 340px;
  }
  
  .stat-overview {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .tree-map-optimized {
    flex-direction: column;
  }
  
  .sidebar-panel {
    width: 100%;
    height: auto;
    max-height: 50vh;
    order: 2;
  }
  
  .map-section {
    height: 50vh;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 5px;
    align-items: flex-start;
  }
  
  .map-controls {
    top: 10px;
    right: 10px;
  }
  
  .layer-control {
    top: 70px;
    right: 10px;
  }
  
  .zoom-control {
    bottom: 80px;
    right: 10px;
  }
}

@media (max-width: 480px) {
  .filter-actions {
    flex-direction: column;
  }
  
  .action-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .footer-content {
    font-size: 11px;
  }
}
</style>

<style>
/* 全局样式修复 */
.amap-marker-label {
  border: none !important;
  background: none !important;
}

.amap-info-content {
  border-radius: 8px !important;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid #e4e7ed !important;
}

.amap-info-sharp {
  display: none !important;
}
</style>