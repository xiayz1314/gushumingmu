// src/api/index.js
import * as ancientTreeApi from './ancientTree'

// 导出所有 API 模块
export default {
  ancientTree: ancientTreeApi.ancientTreeApi,
  stats: ancientTreeApi.ancientTreeStatsApi
}