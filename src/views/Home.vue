<template>
  <div class="home-container enhanced-bg">
    <!-- 欢迎 + 天气 -->
    <div class="header-bar animate-fade-in">
      <div class="welcome">{{ welcomeText }}</div>

      <el-card class="weather-card glass" shadow="hover">
        <div v-if="weather">
          <div class="weather-row">
            <i :class="'qi-' + (weather.icon || '100')" class="weather-icon-font"></i>
            <div class="weather-info">
              <div class="weather-city">{{ weather.city }}</div>
              <div class="weather-desc">{{ weather.weather }}</div>
            </div>
            <div class="weather-temp">{{ weather.temperature }}°C</div>
          </div>
          <div class="weather-sub">
            湿度 {{ weather.humidity }}% ｜ 风力 {{ weather.windPower }}级 ｜ {{ weather.windDir }}
          </div>
        </div>
        <div v-else class="weather-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          正在获取天气信息...
        </div>
      </el-card>
    </div>

    <!-- 顶部统计区 -->
    <el-row :gutter="20" class="top-cards animate-slide-up">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in statList" :key="item.title">
        <el-card shadow="hover" class="stat-card glass">
          <div class="stat-title">{{ item.title }}</div>
          <!-- 数据加载状态 -->
          <div v-if="dataLoading" class="stat-value-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
          </div>
          <div v-else-if="dataError" class="stat-value-error">--</div>
          <div v-else class="stat-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="main-body">
      <!-- 左侧导航 -->
      <el-col :xs="24" :md="6" class="animate-slide-left">
        <el-card shadow="hover" class="menu-card glass">
          <div class="menu-title">功能导航</div>
          <el-menu router class="menu-list">
            <el-menu-item index="/treeList">
              <el-icon><Document /></el-icon>
              <span>古树信息管理</span>
            </el-menu-item>
            <el-menu-item index="/treemap">
              <el-icon><Location /></el-icon>
              <span>古树地图</span>
            </el-menu-item>
            <el-menu-item index="/inspection">
              <el-icon><Search /></el-icon>
              <span>巡查管理</span>
            </el-menu-item>
            <el-menu-item index="/report">
              <el-icon><DataAnalysis /></el-icon>
              <span>统计分析</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧图表：使用后端统计数据 -->
      <el-col :xs="24" :md="18" class="animate-slide-right">
        <!-- 第一行：柱状图 -->
        <el-row :gutter="20" class="chart-row">
          <el-col :span="24">
            <el-card shadow="hover" class="chart-card glass" key="tree-chart">
              <div class="chart-title">树种数量统计（柱状图）</div>
              <div class="chart-container">
                <div v-show="dataLoading" class="chart-state chart-loading">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <div>正在加载数据...</div>
                </div>
                <div v-show="dataError" class="chart-state chart-error">
                  <el-icon><WarningFilled /></el-icon>
                  <div>数据加载失败</div>
                </div>
                <div v-show="!dataLoading && !dataError && (!statsData.speciesDistribution || statsData.speciesDistribution.length === 0)" 
                     class="chart-state chart-empty">
                  <el-icon><DataLine /></el-icon>
                  <div>暂无树种数据</div>
                </div>
                <!-- 图表渲染区域 -->
                <div v-show="!dataLoading && !dataError && statsData.speciesDistribution && statsData.speciesDistribution.length > 0" 
                     ref="treeTypeChartRef" 
                     class="chart-render-area">
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 第二行：饼图和环形图 -->
        <el-row :gutter="20" class="chart-row">
          <el-col :xs="24" :md="12">
            <el-card shadow="hover" class="chart-card glass" key="health-chart">
              <div class="chart-title">健康状况分布（饼图）</div>
              <div class="chart-container">
                <div v-show="dataLoading" class="chart-state chart-loading">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <div>加载中...</div>
                </div>
                <div v-show="dataError" class="chart-state chart-error">
                  <el-icon><WarningFilled /></el-icon>
                  <div>数据加载失败</div>
                </div>
                <div v-show="!dataLoading && !dataError && (!statsData.healthDistribution || Object.keys(statsData.healthDistribution).length === 0)" 
                     class="chart-state chart-empty">
                  <el-icon><DataLine /></el-icon>
                  <div>暂无健康数据</div>
                </div>
                <div v-show="!dataLoading && !dataError && statsData.healthDistribution && Object.keys(statsData.healthDistribution).length > 0" 
                     ref="healthChartRef" 
                     class="chart-render-area">
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-card shadow="hover" class="chart-card glass" key="level-chart">
              <div class="chart-title">保护等级占比（环形图）</div>
              <div class="chart-container">
                <div v-show="dataLoading" class="chart-state chart-loading">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <div>加载中...</div>
                </div>
                <div v-show="dataError" class="chart-state chart-error">
                  <el-icon><WarningFilled /></el-icon>
                  <div>数据加载失败</div>
                </div>
                <div v-show="!dataLoading && !dataError && (!statsData.levelDistribution || Object.keys(statsData.levelDistribution).length === 0)" 
                     class="chart-state chart-empty">
                  <el-icon><DataLine /></el-icon>
                  <div>暂无等级数据</div>
                </div>
                <div v-show="!dataLoading && !dataError && statsData.levelDistribution && Object.keys(statsData.levelDistribution).length > 0" 
                     ref="levelChartRef" 
                     class="chart-render-area">
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance, nextTick, computed, onUnmounted, reactive } from "vue";
import * as echarts from "echarts";
import { 
  Document, 
  Location, 
  Loading, 
  WarningFilled, 
  DataLine,
  Search,
  DataAnalysis
} from "@element-plus/icons-vue";
import api from '@/api'

// 引入和风天气图标字体样式
import 'qweather-icons/font/qweather-icons.css';

// 获取实例代理
const { proxy } = getCurrentInstance() || {};

// 存储ECharts实例，用于销毁
const chartInstances = ref([]);

// 图表DOM引用
const treeTypeChartRef = ref(null);
const healthChartRef = ref(null);
const levelChartRef = ref(null);

/* ------------------- 欢迎词 ------------------- */
const welcomeText = ref("");
const initWelcome = () => {
  const hour = new Date().getHours();
  if (hour < 11) welcomeText.value = "🌤️ 早上好，祝您一天顺利！";
  else if (hour < 14) welcomeText.value = "☀️ 中午好，注意午休！";
  else if (hour < 18) welcomeText.value = "🌥️ 下午好，继续加油！";
  else welcomeText.value = "🌙 晚上好，注意休息！";
};

/* ------------------- 和风天气API配置 ------------------- */
const QWeatherConfig = reactive({
  key: import.meta.env.VITE_QWEATHER_KEY || "",
  host: import.meta.env.VITE_QWEATHER_HOST || "https://devapi.qweather.com",
  defaultCity: "苏州"
});

const weather = ref(null);
const isNightMode = ref(false);

const checkNightMode = () => {
  const hour = new Date().getHours();
  isNightMode.value = hour >= 19 || hour <= 6;
};

/* ------------------- 和风天气API调用函数 ------------------- */
const getLocationIdByCity = async (cityName) => {
  if (!QWeatherConfig.key) {
    console.error("和风天气API Key未配置");
    return null;
  }

  try {
    const url = `${QWeatherConfig.host}/geo/v2/city/lookup?location=${encodeURIComponent(cityName)}&key=${QWeatherConfig.key}`;
    const response = await fetch(url);
    const data = await response.json();
    
    if (data.code === "200" && data.location && data.location.length > 0) {
      return {
        id: data.location[0].id,
        name: data.location[0].name,
        adm2: data.location[0].adm2
      };
    } else {
      console.error("获取locationId失败:", data.code, data.message);
      return null;
    }
  } catch (error) {
    console.error("获取locationId失败:", error);
    return null;
  }
};

const getLocationIdByCoords = async (longitude, latitude) => {
  if (!QWeatherConfig.key) {
    console.error("和风天气API Key未配置");
    return null;
  }

  try {
    const url = `${QWeatherConfig.host}/geo/v2/city/lookup?location=${longitude},${latitude}&key=${QWeatherConfig.key}`;
    const response = await fetch(url);
    const data = await response.json();
    
    if (data.code === "200" && data.location && data.location.length > 0) {
      return {
        id: data.location[0].id,
        name: data.location[0].name,
        adm2: data.location[0].adm2
      };
    } else {
      console.error("根据坐标获取locationId失败:", data.code, data.message);
      return null;
    }
  } catch (error) {
    console.error("根据坐标获取locationId失败:", error);
    return null;
  }
};

const loadWeatherByLocationId = async (locationInfo) => {
  if (!QWeatherConfig.key || !locationInfo) {
    setDefaultWeatherData(locationInfo?.adm2 || QWeatherConfig.defaultCity);
    return;
  }

  try {
    const url = `${QWeatherConfig.host}/v7/weather/now?location=${locationInfo.id}&key=${QWeatherConfig.key}`;
    const response = await fetch(url);
    const data = await response.json();
    
    if (data.code === "200") {
      const now = data.now;
      weather.value = {
        city: locationInfo.adm2 || locationInfo.name,
        weather: now.text,
        temperature: now.temp,
        humidity: now.humidity,
        windPower: now.windScale,
        windDir: now.windDir,
        icon: now.icon,
        feelsLike: now.feelsLike,
        pressure: now.pressure,
        vis: now.vis,
        updateTime: data.updateTime
      };
    } else {
      console.error("天气接口错误:", data.code, data.message);
      setDefaultWeatherData(locationInfo.adm2 || locationInfo.name);
    }
  } catch (error) {
    console.error("获取天气失败:", error);
    setDefaultWeatherData(locationInfo.adm2 || locationInfo.name);
  }
};

const setDefaultWeatherData = (cityName) => {
  weather.value = {
    city: cityName,
    weather: "晴",
    temperature: "22",
    humidity: "65",
    windPower: "2",
    windDir: "东南风",
    icon: isNightMode.value ? "150" : "100"
  };
};

const loadUserLocationWeather = async () => {
  checkNightMode();
  
  if (!navigator.geolocation) {
    const locationInfo = await getLocationIdByCity(QWeatherConfig.defaultCity);
    if (locationInfo) await loadWeatherByLocationId(locationInfo);
    return;
  }

  navigator.geolocation.getCurrentPosition(
    async (pos) => {
      try {
        const { longitude, latitude } = pos.coords;
        const locationInfo = await getLocationIdByCoords(longitude, latitude);
        
        if (locationInfo) await loadWeatherByLocationId(locationInfo);
        else {
          const defaultLocation = await getLocationIdByCity(QWeatherConfig.defaultCity);
          if (defaultLocation) await loadWeatherByLocationId(defaultLocation);
        }
      } catch (error) {
        console.error("定位解析失败:", error);
        const defaultLocation = await getLocationIdByCity(QWeatherConfig.defaultCity);
        if (defaultLocation) await loadWeatherByLocationId(defaultLocation);
      }
    },
    (err) => {
      console.error("定位失败:", err.message);
      getLocationIdByCity(QWeatherConfig.defaultCity).then(locationInfo => {
        if (locationInfo) loadWeatherByLocationId(locationInfo);
      });
    }
  );
};

/* ------------------- 数据状态管理 ------------------- */
const dataLoading = ref(true);
const dataError = ref(false);
const statsData = ref({});

/* ------------------- 统计和图表 ------------------- */
const statList = computed(() => {
  const basic = statsData.value.basicStatistics || {};
  return [
    { title: "古树总数", value: basic.total || 0 },
    { title: "一级保护古树", value: basic.level1 || 0 },
    { title: "濒危古树", value: basic.danger || 0 },
    { title: "今日巡查", value: basic.inspectToday || 0 },
  ];
});

/* ------------------- ECharts 初始化 ------------------- */
const initEchart = (el, option) => {
  if (!el) {
    console.error('[initEchart] 错误: 图表容器不存在');
    return null;
  }

  // 检查并销毁已存在的实例
  const existingChart = echarts.getInstanceByDom(el);
  if (existingChart && !existingChart.isDisposed()) {
    existingChart.dispose();
  }

  try {
    const chart = echarts.init(el);
    
    // 设置配置项
    chart.setOption(option, true);
    
    // 存储实例引用
    chartInstances.value.push(chart);
    
    // 响应窗口大小变化
    const resizeHandler = () => {
      if (chart && !chart.isDisposed()) {
        chart.resize();
      }
    };
    window.addEventListener('resize', resizeHandler);
    chart._resizeHandler = resizeHandler;
    
    return chart;
  } catch (error) {
    console.error('[initEchart] 图表初始化失败:', error);
    return null;
  }
};

// 加载统计数据
const loadStatistics = async () => {
  dataLoading.value = true;
  dataError.value = false;

  try {
    const res = await api.stats.getAllStatistics();
    
    if (res && res.code === 200) {
      statsData.value = res.data || {};
      
      // 等待DOM渲染完成
      await nextTick();
      await new Promise(resolve => setTimeout(resolve, 100));
      
      // 渲染图表
      if (statsData.value.speciesDistribution || statsData.value.healthDistribution || statsData.value.levelDistribution) {
        renderCharts();
      }
    } else {
      console.error('获取统计数据失败:', res?.msg);
      dataError.value = true;
    }
  } catch (error) {
    console.error("加载统计数据失败:", error);
    dataError.value = true;
  } finally {
    dataLoading.value = false;
  }
};

// 渲染图表
const renderCharts = () => {
  console.log('开始渲染图表，统计数据:', statsData.value);
  
  // 1. 先清理旧实例
  chartInstances.value.forEach(chart => {
    if (chart && !chart.isDisposed()) {
      if (chart._resizeHandler) {
        window.removeEventListener('resize', chart._resizeHandler);
      }
      chart.dispose();
    }
  });
  chartInstances.value = [];
  
  // 2. 逐一渲染图表
  if (statsData.value.speciesDistribution && treeTypeChartRef.value) {
    renderTreeTypeChart();
  }
  
  if (statsData.value.healthDistribution && healthChartRef.value) {
    renderHealthChart();
  }
  
  if (statsData.value.levelDistribution && levelChartRef.value) {
    renderLevelChart();
  }
  
  // 3. 所有图表渲染完成后，全局触发resize
  setTimeout(() => {
    window.dispatchEvent(new Event('resize'));
  }, 300);
};

// 渲染树种统计柱状图（使用后端数据）
const renderTreeTypeChart = () => {
  const container = treeTypeChartRef.value;
  if (!container) {
    console.error('[柱状图] 错误: 容器引用为空');
    return;
  }

  try {
    const speciesData = statsData.value.speciesDistribution || [];
    
    if (speciesData.length === 0) {
      console.warn('[柱状图] 警告: 无有效树种数据');
      return;
    }
    
    const speciesNames = speciesData.map(item => item.species);
    const speciesValues = speciesData.map(item => item.count);
    
    const option = {
      tooltip: { 
        trigger: "axis",
        formatter: "{b}: {c}棵"
      },
      grid: {
        left: '5%',
        right: '5%',
        bottom: '10%',
        top: '10%',
        containLabel: true
      },
      xAxis: { 
        type: "category", 
        data: speciesNames,
        axisLabel: {
          interval: 0,
          rotate: speciesNames.some(name => name.length > 3) ? 30 : 0,
          fontSize: 11
        }
      },
      yAxis: { 
        type: "value",
        name: "数量（棵）",
        minInterval: 1
      },
      series: [{
        type: "bar",
        data: speciesValues,
        itemStyle: { 
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#4CAF50" },
            { offset: 1, color: "#2E7D32" }
          ])
        },
        label: {
          show: true,
          position: "top",
          color: "#2E7D32",
          fontSize: 11
        },
        barWidth: '60%'
      }]
    };
    
    initEchart(container, option);
    console.log('✅ [柱状图] 初始化成功');
  } catch (error) {
    console.error('❌ [柱状图] 渲染过程中出错:', error);
  }
};

// 渲染健康状况饼图（使用后端数据）
const renderHealthChart = () => {
  const container = healthChartRef.value;
  if (!container) {
    console.error('[饼图] 错误: 容器引用为空');
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
          color: name === "健康" ? "#4CAF50" : name === "一般" ? "#FFC107" : "#F44336"
        }
      }));
    
    console.log('[饼图] 数据:', chartData);
    
    if (chartData.length === 0) {
      console.warn('[饼图] 警告: 无健康状况数据');
      return;
    }
    
    const option = {
      tooltip: { 
        trigger: "item",
        formatter: "{a} <br/>{b}: {c}棵 ({d}%)"
      },
      legend: {
        orient: "vertical",
        left: "left",
        top: "center"
      },
      series: [{
        name: "健康状况",
        type: "pie",
        radius: ["40%", "70%"],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: "{b}: {c}棵\n({d}%)",
          fontSize: 12
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: "bold"
          }
        },
        labelLine: { show: true },
        data: chartData
      }]
    };
    
    initEchart(container, option);
    console.log('✅ [饼图] 初始化成功');
  } catch (error) {
    console.error('❌ [饼图] 渲染过程中出错:', error);
  }
};

// 渲染保护等级环形图（使用后端数据）
const renderLevelChart = () => {
  const container = levelChartRef.value;
  if (!container) {
    console.error('[环形图] 错误: 容器引用为空');
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
          color: name === "一级" ? "#1976D2" : name === "二级" ? "#2196F3" : "#64B5F6"
        }
      }));
    
    console.log('[环形图] 数据:', chartData);
    
    if (chartData.length === 0) {
      console.warn('[环形图] 警告: 无保护等级数据');
      return;
    }
    
    const option = {
      tooltip: { 
        trigger: "item",
        formatter: "{a} <br/>{b}: {c}棵 ({d}%)"
      },
      legend: {
        orient: "vertical",
        right: "right",
        top: "center"
      },
      series: [{
        name: "保护等级",
        type: "pie",
        radius: ["40%", "70%"],
        center: ['45%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: "{b}: {c}棵\n({d}%)",
          fontSize: 12
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: "bold"
          }
        },
        labelLine: { show: true },
        data: chartData
      }]
    };
    
    initEchart(container, option);
    console.log('✅ [环形图] 初始化成功');
  } catch (error) {
    console.error('❌ [环形图] 渲染过程中出错:', error);
  }
};

/* ------------------- 生命周期 ------------------- */
onMounted(() => {
  initWelcome();
  checkNightMode();
  loadUserLocationWeather();
  loadStatistics();
  
  // 页面加载完成后触发resize
  setTimeout(() => {
    window.dispatchEvent(new Event('resize'));
  }, 1000);
  
  // 自动刷新定时器（30分钟）
  const weatherInterval = setInterval(loadUserLocationWeather, 30 * 60 * 1000);
  
  // 组件卸载清理
  onUnmounted(() => {
    clearInterval(weatherInterval);
    
    // 销毁所有ECharts实例
    chartInstances.value.forEach(chart => {
      if (chart && !chart.isDisposed()) {
        if (chart._resizeHandler) {
          window.removeEventListener('resize', chart._resizeHandler);
        }
        chart.dispose();
      }
    });
    chartInstances.value = [];
  });
});
</script>

<style scoped>
/* 渐变背景 */
.enhanced-bg {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  min-height: 100vh;
  padding: 20px;
  overflow-x: hidden;
}

/* 玻璃拟态卡片 */
.glass {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.1);
}

/* 头部栏样式 */
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.welcome {
  font-size: 24px;
  font-weight: bold;
  color: #2e7d32;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

/* 天气卡片样式 */
.weather-card {
  width: 380px;
  max-width: 100%;
  transition: all 0.3s ease;
}

.weather-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.2);
}

.weather-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.weather-info {
  flex: 1;
}

.weather-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  padding: 20px 0;
}

.weather-loading .el-icon {
  margin-right: 8px;
  animation: rotating 2s linear infinite;
}

/* 和风天气图标字体样式 */
.weather-icon-font {
  font-size: 40px;
  margin-right: 15px;
  color: #FFA726;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 60px;
}

.weather-city {
  font-size: 20px;
  font-weight: bold;
  color: #1a237e;
  margin-bottom: 4px;
}

.weather-desc {
  font-size: 14px;
  color: #546e7a;
}

.weather-temp {
  font-size: 32px;
  font-weight: bold;
  margin-left: 15px;
  color: #e53935;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}

.weather-sub {
  font-size: 13px;
  color: #78909c;
  text-align: right;
  padding-top: 8px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

/* 统计卡片样式 */
.top-cards {
  margin-bottom: 25px;
}

.stat-card {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  margin-bottom: 15px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.15);
}

.stat-title {
  font-size: 16px;
  color: #546e7a;
  margin-bottom: 8px;
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #2e7d32;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-value-loading, .stat-value-error {
  font-size: 32px;
  color: #999;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value-loading .el-icon {
  animation: rotating 2s linear infinite;
}

/* 菜单卡片样式 */
.menu-card {
  height: auto;
  min-height: 200px;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.15);
}

.menu-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  color: #1a237e;
}

.menu-list {
  border: none;
  background: transparent;
}

.menu-list :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  height: 50px;
}

.menu-list :deep(.el-menu-item:hover) {
  background: rgba(46, 125, 50, 0.1);
  color: #2e7d32;
}

.menu-list :deep(.el-menu-item.is-active) {
  background: rgba(46, 125, 50, 0.2);
  color: #2e7d32;
  font-weight: bold;
}

/* 主内容区域 */
.main-body {
  margin-top: 20px;
  min-height: 600px;
}

/* 图表行样式 */
.chart-row {
  margin-bottom: 20px;
}

/* 图表卡片样式 */
.chart-card {
  height: 400px; /* 固定高度，确保图表有足够空间 */
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.15);
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  color: #1a237e;
  flex-shrink: 0;
}

/* 关键修复：图表容器设置 */
.chart-container {
  flex: 1; /* 占据剩余空间 */
  position: relative;
  width: 100%;
  min-height: 320px; /* 最小高度确保显示 */
}

.chart-render-area {
  height: 100%;
  width: 100%;
  min-height: 300px;
}

/* 图表状态样式 */
.chart-state {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 1;
  border-radius: 10px;
}

.chart-loading .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
  animation: rotating 2s linear infinite;
}

.chart-error .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
  color: #F56C6C;
}

.chart-empty .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
  color: #909399;
}

/* 动画效果 */
.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.7s ease-out;
}

.animate-slide-left {
  animation: slideLeft 0.7s ease-out;
}

.animate-slide-right {
  animation: slideRight 0.7s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideLeft {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideRight {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式适配 */
@media (max-width: 992px) {
  .header-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .weather-card {
    width: 100%;
    margin-top: 10px;
  }
  
  .main-body .el-col {
    width: 100%;
  }
  
  .menu-card {
    margin-bottom: 20px;
  }
  
  .chart-card {
    height: 380px;
  }
  
  .chart-container {
    min-height: 300px;
  }
}

@media (max-width: 768px) {
  .enhanced-bg {
    padding: 15px;
  }
  
  .welcome {
    font-size: 20px;
  }
  
  .top-cards .el-col {
    margin-bottom: 10px;
  }
  
  .stat-card {
    height: 100px;
  }
  
  .stat-title {
    font-size: 14px;
  }
  
  .stat-value,
  .stat-value-loading,
  .stat-value-error {
    font-size: 28px;
  }
  
  .weather-icon-font {
    font-size: 35px;
    min-width: 50px;
  }
  
  .weather-city {
    font-size: 18px;
  }
  
  .weather-temp {
    font-size: 28px;
  }
  
  .chart-card {
    height: 360px;
  }
  
  .chart-container {
    min-height: 280px;
  }
  
  .chart-title {
    font-size: 15px;
  }
}

@media (max-width: 576px) {
  .welcome {
    font-size: 18px;
  }
  
  .weather-card {
    width: 100%;
  }
  
  .weather-row {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .weather-icon-font {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .weather-temp {
    margin-left: 0;
    margin-top: 10px;
    font-size: 24px;
  }
  
  .weather-sub {
    text-align: left;
    font-size: 12px;
  }
  
  .menu-card {
    min-height: 180px;
  }
  
  .menu-list :deep(.el-menu-item) {
    height: 45px;
    font-size: 14px;
  }
  
  .chart-card {
    height: 320px;
  }
  
  .chart-container {
    min-height: 240px;
  }
  
  .chart-title {
    font-size: 14px;
  }
}
</style>