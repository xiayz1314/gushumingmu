// api/ancientTree.js
// 1. 首先修正导入路径（移除 .ts 后缀）
import request from '@/utils/request'

export const ancientTreeApi = {
  // 获取分页列表
  getAncientTreePage: (page, size, treeName, species) => {
    // 2. 使用 request.get 方法
    return request.get('/ancient-trees/page', {
      params: { page, size, treeName, species }
    })
  },
  // 获取详情
  getAncientTreeById: (id) => {
    return request.get(`/ancient-trees/${id}`)
  },
  // 新增
  createAncientTree: (data) => {
    return request.post('/ancient-trees', data)
  },
  // 更新
  updateAncientTree: (id, data) => {
    return request.put(`/ancient-trees/${id}`, data)
  },
  // 删除
  deleteAncientTree: (id) => {
    return request.delete(`/ancient-trees/${id}`)
  }
}
// 古树统计API
export const ancientTreeStatsApi = {
  // 获取所有统计数据
  getAllStatistics() {
    return request.get('/ancient-trees/statistics/all')
  },
  
  // 获取基础统计数据
  getBasicStats() {
    return request.get('/ancient-trees/statistics/basic')
  },
  
  // 获取树种分布
  getSpeciesDistribution(limit = 10) {
    return request.get(`/ancient-trees/statistics/species-distribution?limit=${limit}`)
  },
  
  // 获取健康状况分布
  getHealthDistribution() {
    return request.get('/ancient-trees/statistics/health-distribution')
  },
  
  // 获取保护等级分布
  getLevelDistribution() {
    return request.get('/ancient-trees/statistics/level-distribution')
  },
  
  // 获取树龄分布
  getAgeDistribution() {
    return request.get('/ancient-trees/statistics/age-distribution')
  },
  
  // 获取主要树种平均年龄对比
  getSpeciesAgeComparison(limit = 8) {
    return request.get(`/ancient-trees/statistics/species-age-comparison?limit=${limit}`)
  },
  
  // 获取各保护等级健康分布
  getLevelHealthDistribution() {
    return request.get('/ancient-trees/statistics/level-health-distribution')
  },
  
  // 获取地图数据
  getMapData(params) {
    return request.get('/ancient-trees/statistics/map-data', params)
  },
  
  // 清除缓存
  clearCache() {
    return request.post('/ancient-trees/statistics/clear-cache')
  }
}
export default {
  ancientTree: ancientTreeApi,
  ancientTreeStatsApi: ancientTreeStatsApi
}