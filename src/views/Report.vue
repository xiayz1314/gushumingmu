<template>
  <div class="report-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">古树统计分析</h1>
      <div class="page-actions">
        <el-button 
          type="primary" 
          @click="refreshData"
          :loading="loading"
          :icon="Refresh"
        >
          刷新数据
        </el-button>
        <el-button 
          type="success" 
          @click="exportReport"
          :icon="Download"
        >
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-row">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statData.total || 0 }}</div>
            <div class="stat-label">古树总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statData.level1 || 0 }}</div>
            <div class="stat-label">一级保护古树</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statData.danger || 0 }}</div>
            <div class="stat-label">濒危古树</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statData.avgAge || 0 }}年</div>
            <div class="stat-label">平均树龄</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要统计图表 -->
    <div class="main-charts">
      <div class="charts-row">
        <!-- 树种分布柱状图 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">树种数量分布</span>
            <span class="chart-tag">柱状图</span>
          </div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.speciesDistribution || statsData.speciesDistribution.length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.speciesDistribution && statsData.speciesDistribution.length > 0" 
              ref="speciesChartRef" 
              class="chart-render-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>

        <!-- 健康状况饼图 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">健康状况分布</span>
            <span class="chart-tag">饼图</span>
          </div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.healthDistribution || Object.keys(statsData.healthDistribution).length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.healthDistribution && Object.keys(statsData.healthDistribution).length > 0" 
              ref="healthChartRef" 
              class="chart-render-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>
      </div>

      <div class="charts-row">
        <!-- 保护等级环形图 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">保护等级占比</span>
            <span class="chart-tag">环形图</span>
          </div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.levelDistribution || Object.keys(statsData.levelDistribution).length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.levelDistribution && Object.keys(statsData.levelDistribution).length > 0" 
              ref="levelChartRef" 
              class="chart-render-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>

        <!-- 树龄分布折线图 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">树龄分布</span>
            <span class="chart-tag">折线图</span>
          </div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.ageDistribution || statsData.ageDistribution.length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.ageDistribution && statsData.ageDistribution.length > 0" 
              ref="ageChartRef" 
              class="chart-render-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>
      </div>

      <div class="charts-row">
        <!-- 古树位置热力图（模拟数据） -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">古树区域分布</span>
            <span class="chart-tag">热力图</span>
          </div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div class="chart-empty">
              <div class="empty-icon">🗺️</div>
              <div>热力图功能开发中</div>
              <p class="empty-tip">建议使用地图页面查看详细分布</p>
            </div>
          </div>
        </div>

        <!-- 数据表格 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">古树数据概览</span>
            <span class="chart-tag">数据表</span>
          </div>
          <div class="table-container">
            <el-table 
              :data="tableData" 
              stripe 
              style="width: 100%"
              height="300"
              v-loading="loading"
            >
              <el-table-column prop="treeCode" label="编号" width="100" />
              <el-table-column prop="treeName" label="古树名称" />
              <el-table-column prop="species" label="树种" width="120">
                <template #default="scope">
                  {{ scope.row.species?.split(' ')[0] || scope.row.species || '未知' }}
                </template>
              </el-table-column>
              <el-table-column prop="age" label="树龄" width="80">
                <template #default="scope">{{ scope.row.age }}年</template>
              </el-table-column>
              <el-table-column prop="protectionLevel" label="保护等级" width="100">
                <template #default="scope">
                  <el-tag :type="getLevelTagType(scope.row.protectionLevel)" size="small">
                    {{ getLevelText(scope.row.protectionLevel) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="healthStatus" label="健康状况" width="100">
                <template #default="scope">
                  <el-tag :type="getHealthTagType(scope.row.healthStatus)" size="small">
                    {{ getHealthText(scope.row.healthStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div class="table-footer">
              <span class="table-total">共 {{ tableData.length }} 条记录</span>
              <el-button type="primary" link @click="goToTreeList">
                查看全部
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 高级统计 -->
    <div class="advanced-stats">
      <div class="advanced-header">
        <span class="advanced-title">高级统计分析</span>
        <div class="advanced-actions">
          <el-button 
            type="success" 
            @click="exportReport"
            :icon="Download"
          >
            导出报告
          </el-button>
        </div>
      </div>
      <div class="advanced-charts">
        <div class="advanced-chart-container">
          <div class="advanced-chart-title">主要树种平均年龄对比</div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.speciesAgeComparison || statsData.speciesAgeComparison.length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.speciesAgeComparison && statsData.speciesAgeComparison.length > 0" 
              ref="ageCompareChartRef" 
              class="advanced-chart-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>
        <div class="advanced-chart-container">
          <div class="advanced-chart-title">各保护等级健康分布</div>
          <div class="chart-container">
            <div v-show="loading" class="chart-loading">
              <div class="loading-spinner"></div>
              <div>加载中...</div>
            </div>
            <div v-show="error && !loading" class="chart-error">
              <div class="error-icon">!</div>
              <div>数据加载失败</div>
            </div>
            <div v-show="!loading && !error && (!statsData.levelHealthDistribution || Object.keys(statsData.levelHealthDistribution).length === 0)" class="chart-empty">
              <div class="empty-icon">∅</div>
              <div>暂无数据</div>
            </div>
            <div 
              v-show="!loading && !error && statsData.levelHealthDistribution && Object.keys(statsData.levelHealthDistribution).length > 0" 
              ref="levelHealthChartRef" 
              class="advanced-chart-area"
              style="width: 100%; height: 100%;"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import * as echarts from "echarts";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Download, Refresh } from "@element-plus/icons-vue";
import api from '@/api';

const router = useRouter();

// 数据状态
const loading = ref(true);
const error = ref(false);
const statsData = ref({});

// 统计数据
const statData = ref({
  total: 0,
  level1: 0,
  danger: 0,
  avgAge: 0,
  maxAge: 0,
  minAge: 0
});

// 表格数据
const tableData = ref([]);

// 图表引用
const speciesChartRef = ref(null);
const healthChartRef = ref(null);
const levelChartRef = ref(null);
const ageChartRef = ref(null);
const ageCompareChartRef = ref(null);
const levelHealthChartRef = ref(null);

// 图表实例
const chartInstances = [];

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true;
  error.value = false;

  try {
    // 获取统计数据
    const statsRes = await api.stats.getAllStatistics();
    
    if (statsRes.code === 200) {
      statsData.value = statsRes.data || {};
      
      // 更新统计卡片数据
      if (statsData.value.basicStatistics) {
        const basic = statsData.value.basicStatistics;
        statData.value.total = basic.total || 0;
        statData.value.level1 = basic.level1 || 0;
        statData.value.danger = basic.danger || 0;
        statData.value.avgAge = Math.round(basic.avgAge || 0);
        statData.value.maxAge = basic.maxAge || 0;
        statData.value.minAge = basic.minAge || 0;
      }
    } else {
      error.value = true;
      ElMessage.error(statsRes.msg || '获取统计数据失败');
    }

    // 加载表格数据（前5条）
    const listRes = await api.ancientTree.getAncientTreePage(0, 5, "", "");
    if (listRes.code === 200) {
      const records = listRes.data?.records || [];
      tableData.value = records;
    }

    // 等待DOM渲染完成
    await nextTick();
    await new Promise(resolve => setTimeout(resolve, 100));
    
    // 渲染所有图表
    renderAllCharts();
    
    ElMessage.success("数据加载成功");
  } catch (err) {
    console.error("加载数据失败:", err);
    error.value = true;
    ElMessage.error("数据加载失败");
  } finally {
    loading.value = false;
  }
};

// 渲染所有图表
const renderAllCharts = async () => {
  // 清理旧图表
  chartInstances.forEach(chart => {
    if (chart && !chart.isDisposed()) {
      chart.dispose();
    }
  });
  chartInstances.length = 0;
  
  console.log('开始渲染所有图表，统计数据:', statsData.value);
  
  // 等待DOM完全渲染
  await nextTick();
  
  // 逐一检查容器并渲染图表
  const chartRenderers = [
    { ref: speciesChartRef, renderer: renderSpeciesChart, name: '树种分布柱状图' },
    { ref: healthChartRef, renderer: renderHealthChart, name: '健康状况饼图' },
    { ref: levelChartRef, renderer: renderLevelChart, name: '保护等级环形图' },
    { ref: ageChartRef, renderer: renderAgeChart, name: '树龄分布折线图' },
    { ref: ageCompareChartRef, renderer: renderAgeCompareChart, name: '主要树种平均年龄对比图' },
    { ref: levelHealthChartRef, renderer: renderLevelHealthChart, name: '各保护等级健康分布图' }
  ];
  
  for (const { ref, renderer, name } of chartRenderers) {
    if (ref.value) {
      console.log(`渲染${name}...`);
      renderer();
    } else {
      console.warn(`${name}容器不存在，尝试延迟渲染...`);
      // 如果容器不存在，延迟后重试
      setTimeout(() => {
        if (ref.value) {
          renderer();
        } else {
          console.error(`${name}容器仍然不存在，跳过渲染`);
        }
      }, 100);
    }
  }
  
  // 所有图表渲染完成后，全局触发resize
  setTimeout(() => {
    window.dispatchEvent(new Event('resize'));
    console.log('所有图表渲染完成');
  }, 500);
};

// 1. 树种分布柱状图
const renderSpeciesChart = () => {
  const container = speciesChartRef.value;
  if (!container) {
    console.error('树种分布柱状图容器不存在');
    return;
  }

  try {
    const speciesData = statsData.value.speciesDistribution || [];
    
    if (speciesData.length === 0) return;
    
    const speciesNames = speciesData.map(item => item.species);
    const speciesCounts = speciesData.map(item => item.count);

    // 确保容器有尺寸
    if (container.offsetWidth === 0 || container.offsetHeight === 0) {
      container.style.width = '100%';
      container.style.height = '100%';
    }

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: '{b}: {c}棵'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: speciesNames,
        axisLabel: {
          interval: 0,
          rotate: speciesNames.some(name => name.length > 3) ? 30 : 0
        }
      },
      yAxis: {
        type: 'value',
        name: '数量（棵）'
      },
      series: [{
        type: 'bar',
        data: speciesCounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#1890ff' },
            { offset: 1, color: '#69c0ff' }
          ])
        },
        label: {
          show: true,
          position: 'top',
          fontSize: 12
        }
      }]
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('树种分布柱状图渲染成功');
  } catch (err) {
    console.error('渲染树种分布柱状图失败:', err);
  }
};

// 2. 健康状况饼图
const renderHealthChart = () => {
  const container = healthChartRef.value;
  if (!container) {
    console.error('健康状况饼图容器不存在');
    return;
  }

  try {
    const healthData = statsData.value.healthDistribution || {};
    
    const chartData = Object.entries(healthData)
      .filter(([_, value]) => value > 0)
      .map(([name, value]) => ({
        name,
        value: Number(value),
        itemStyle: {
          color: name === "健康" ? "#52c41a" : name === "一般" ? "#faad14" : "#ff4d4f"
        }
      }));
    
    if (chartData.length === 0) return;

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a}<br/>{b}: {c}棵 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center'
      },
      series: [{
        name: '健康状况',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}: {c}棵\n({d}%)',
          fontSize: 12
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: chartData
      }]
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('健康状况饼图渲染成功');
  } catch (err) {
    console.error('渲染健康状况饼图失败:', err);
  }
};

// 3. 保护等级环形图
const renderLevelChart = () => {
  const container = levelChartRef.value;
  if (!container) {
    console.error('保护等级环形图容器不存在');
    return;
  }

  try {
    const levelData = statsData.value.levelDistribution || {};
    
    const chartData = Object.entries(levelData)
      .filter(([_, value]) => value > 0)
      .map(([name, value]) => ({
        name,
        value: Number(value),
        itemStyle: {
          color: name === "一级" ? "#1890ff" : name === "二级" ? "#52c41a" : "#faad14"
        }
      }));
    
    if (chartData.length === 0) return;

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a}<br/>{b}: {c}棵 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 'right',
        top: 'center'
      },
      series: [{
        name: '保护等级',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['45%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}: {c}棵\n({d}%)',
          fontSize: 12
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: chartData
      }]
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('保护等级环形图渲染成功');
  } catch (err) {
    console.error('渲染保护等级环形图失败:', err);
  }
};

// 4. 树龄分布折线图
const renderAgeChart = () => {
  const container = ageChartRef.value;
  if (!container) {
    console.error('树龄分布折线图容器不存在');
    return;
  }

  try {
    const ageData = statsData.value.ageDistribution || [];
    
    if (ageData.length === 0) return;
    
    const ageRanges = ageData.map(item => item.range);
    const ageCounts = ageData.map(item => item.count);

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: '{b}: {c}棵'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ageRanges
      },
      yAxis: {
        type: 'value',
        name: '数量（棵）'
      },
      series: [{
        name: '树龄分布',
        type: 'line',
        data: ageCounts,
        smooth: true,
        lineStyle: {
          width: 3
        },
        itemStyle: {
          color: '#722ed1'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(114, 46, 209, 0.3)' },
            { offset: 1, color: 'rgba(114, 46, 209, 0.1)' }
          ])
        }
      }]
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('树龄分布折线图渲染成功');
  } catch (err) {
    console.error('渲染树龄分布折线图失败:', err);
  }
};

// 5. 主要树种平均年龄对比图
const renderAgeCompareChart = () => {
  const container = ageCompareChartRef.value;
  if (!container) {
    console.error('主要树种平均年龄对比图容器不存在');
    return;
  }

  try {
    const speciesAgeData = statsData.value.speciesAgeComparison || [];
    
    if (speciesAgeData.length === 0) return;

    const speciesNames = speciesAgeData.map(item => item.species);
    const avgAges = speciesAgeData.map(item => item.avgAge);

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: '{b}: {c}年'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: speciesNames,
        axisLabel: {
          rotate: 45
        }
      },
      yAxis: {
        type: 'value',
        name: '平均年龄（年）'
      },
      series: [{
        type: 'bar',
        data: avgAges,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#ff7a45' },
            { offset: 1, color: '#ff9c6e' }
          ])
        },
        label: {
          show: true,
          position: 'top'
        }
      }]
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('主要树种平均年龄对比图渲染成功');
  } catch (err) {
    console.error('渲染主要树种平均年龄对比图失败:', err);
  }
};

// 6. 各保护等级健康分布图
const renderLevelHealthChart = () => {
  const container = levelHealthChartRef.value;
  if (!container) {
    console.error('各保护等级健康分布图容器不存在');
    return;
  }

  try {
    const levelHealthData = statsData.value.levelHealthDistribution || {};
    
    const levels = ["一级", "二级", "三级"];
    const healthTypes = ["健康", "一般", "濒危"];
    
    // 检查是否有数据
    const hasData = levels.some(level => 
      healthTypes.some(health => levelHealthData[level]?.[health] > 0)
    );
    
    if (!hasData) return;

    const series = healthTypes.map((health, index) => ({
      name: health,
      type: "bar",
      stack: "total",
      data: levels.map(level => levelHealthData[level]?.[health] || 0),
      itemStyle: {
        color: health === "健康" ? "#52c41a" : 
               health === "一般" ? "#faad14" : "#ff4d4f"
      }
    }));

    const chart = echarts.init(container);
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          let result = params[0].name + '<br/>';
          params.forEach(item => {
            result += `${item.marker}${item.seriesName}: ${item.value}棵<br/>`;
          });
          return result;
        }
      },
      legend: {
        data: healthTypes,
        bottom: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: levels
      },
      yAxis: {
        type: 'value',
        name: '数量（棵）'
      },
      series: series
    };

    chart.setOption(option);
    chartInstances.push(chart);
    console.log('各保护等级健康分布图渲染成功');
  } catch (err) {
    console.error('渲染各保护等级健康分布图失败:', err);
  }
};

// 辅助函数
const getHealthText = (status) => {
  switch (status) {
    case 1: return '良好';
    case 2: return '一般';
    case 3: return '濒危';
    default: return '未知';
  }
};

const getHealthTagType = (status) => {
  switch (status) {
    case 1: return 'success';
    case 2: return 'warning';
    case 3: return 'danger';
    default: return 'info';
  }
};

const getLevelText = (level) => {
  switch (level) {
    case 1: return '一级';
    case 2: return '二级';
    case 3: return '三级';
    default: return '未知';
  }
};

const getLevelTagType = (level) => {
  switch (level) {
    case 1: return 'danger';
    case 2: return 'warning';
    case 3: return 'success';
    default: return 'info';
  }
};

// 导出报告
const exportReport = () => {
  ElMessage.info('导出功能开发中，敬请期待');
};

// 跳转到古树列表
const goToTreeList = () => {
  router.push('/treeList');
};

// 刷新数据
const refreshData = () => {
  loadStatistics();
};

// 窗口大小变化时重绘图表
const handleResize = () => {
  chartInstances.forEach(chart => {
    if (chart && !chart.isDisposed()) {
      chart.resize();
    }
  });
};

// 生命周期
onMounted(() => {
  console.log('Report页面已挂载，开始加载数据...');
  loadStatistics();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  chartInstances.forEach(chart => {
    if (chart && !chart.isDisposed()) {
      chart.dispose();
    }
  });
  console.log('Report页面已卸载，清理图表实例');
});
</script>

<style scoped>
/* 基础样式 */
.report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.page-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-cards {
  margin-bottom: 24px;
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图表区域 */
.main-charts {
  margin-bottom: 24px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

@media (max-width: 1200px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.chart-tag {
  padding: 4px 8px;
  background-color: #ecf5ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.chart-container {
  flex: 1;
  position: relative;
  min-height: 300px;
}

.chart-render-area {
  width: 100%;
  height: 100%;
}

.chart-loading,
.chart-error,
.chart-empty {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  background-color: rgba(255, 255, 255, 0.8);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

.error-icon {
  width: 40px;
  height: 40px;
  background-color: #f56c6c;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.empty-icon {
  width: 40px;
  height: 40px;
  background-color: #909399;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.empty-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 数据表格 */
.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-total {
  font-size: 14px;
  color: #909399;
}

/* 高级统计 */
.advanced-stats {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.advanced-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.advanced-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.advanced-actions {
  display: flex;
  gap: 12px;
}

.advanced-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 16px;
}

@media (max-width: 1200px) {
  .advanced-charts {
    grid-template-columns: 1fr;
  }
}

.advanced-chart-container {
  height: 300px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.advanced-chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16px;
  text-align: center;
}

.advanced-chart-area {
  width: 100%;
  height: calc(100% - 30px);
}

/* 动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .report-container {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .stat-row {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
  
  .chart-card {
    height: 350px;
  }
  
  .advanced-charts {
    grid-template-columns: 1fr;
  }
  
  .advanced-chart-container {
    height: 250px;
  }
}

@media (max-width: 576px) {
  .stat-row {
    grid-template-columns: 1fr 1fr;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .chart-card {
    height: 320px;
    padding: 16px;
  }
}
</style>