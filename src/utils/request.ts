import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import router from "../router"; // 引入路由

// 创建axios实例
const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json;charset=utf-8"
  }
});

// 请求拦截器：添加Token
request.interceptors.request.use(
  (config) => {
    // 从localStorage获取Token（不依赖Pinia）
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理错误
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 统一处理后端响应（假设后端返回 {code, msg, data}）
    if (res.code !== 200) {
      ElMessage.error(res.msg || "操作失败");
      return Promise.reject(res);
    }
    return res; // 直接返回data，简化使用
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // Token过期：清除localStorage并跳转登录页
          localStorage.removeItem("token");
          localStorage.removeItem("username");
          ElMessageBox.alert("登录已过期，请重新登录", "提示", {
            confirmButtonText: "确定",
            callback: () => {
              router.push("/login"); // 跳转到登录页
            }
          });
          break;
        case 403:
          ElMessage.error("权限不足，无法操作");
          break;
        case 404:
          ElMessage.error("请求的接口不存在");
          break;
        case 500:
          ElMessage.error("服务器内部错误，请稍后重试");
          break;
        default:
          ElMessage.error(`请求错误: ${error.response.data?.msg || error.message}`);
      }
    } else {
      ElMessage.error("网络连接异常，请检查网络");
    }
    return Promise.reject(error);
  }
);

// 导出常用请求方法
export default {
  get: (url: string, params?: any) => request.get(url, { params }),
  post: (url: string, data?: any) => request.post(url, data),
  put: (url: string, data?: any) => request.put(url, data),
  delete: (url: string, params?: any) => request.delete(url, { params }),
  // 表单提交（form-data格式）
  postForm: (url: string, data?: any) => {
    const formData = new FormData();
    Object.keys(data || {}).forEach(key => {
      formData.append(key, data[key]);
    });
    return request.post(url, formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });
  }
};