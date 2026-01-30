import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { Avatar, User } from '@element-plus/icons-vue' // 导入常用图标
import api from './api'
// 创建应用实例
const app = createApp(App)

// 全局注册图标
app.component('Avatar', Avatar)
app.component('User', User)

// 注册插件
app.use(ElementPlus)
app.use(router)

app.config.globalProperties.$api = api
// 挂载应用
app.mount('#app')