import axios from "axios";
import { ElMessage } from "element-plus"; // 引入el 提示框，这个项目里用什么组件库这里引什么

// 请求白名单
const WHITE_URL_LIST = [];

// 定义axios配置
const request = axios.create({
  baseURL: "/", // 请求地址，这里是动态赋值的的环境变量
  withCredentials: true, // 开启跨域身份凭证
  method: "post",
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
  timeout: 1000 * 60, // 设置接口超时时间, 60秒
});

//http request 拦截器
axios.interceptors.request.use(
  (config) => {
    // 配置请求头
    if (judgeRequestUrl(config.url)) {
      config.headers.Authorization = `Bearer token`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

//http response 拦截器
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const { response } = error;
    if (response) {
      // 请求已发出，但是不在2xx的范围
      showMessage(response.status); // 传入响应码，匹配响应码对应信息
      return Promise.reject(response.data);
    } else {
      ElMessage.warning("网络连接异常,请稍后再试!");
    }
  }
);

const judgeRequestUrl = (url) => {
  return !WHITE_URL_LIST.includes(url);
};

// 返回状态码信息
const showMessage = (status) => {
  let message = "";
  switch (status) {
    case 400:
      message = "请求错误(400)";
      break;
    case 401:
      message = "未授权，请重新登录(401)";
      break;
    case 403:
      message = "拒绝访问(403)";
      break;
    case 404:
      message = "请求出错(404)";
      break;
    case 408:
      message = "请求超时(408)";
      break;
    case 500:
      message = "服务器错误(500)";
      break;
    case 501:
      message = "服务未实现(501)";
      break;
    case 502:
      message = "网络错误(502)";
      break;
    case 503:
      message = "服务不可用(503)";
      break;
    case 504:
      message = "网络超时(504)";
      break;
    case 505:
      message = "HTTP版本不受支持(505)";
      break;
    default:
      message = `连接出错(${status})!`;
  }
  return `${message}，请检查网络或联系管理员！`;
};

// 封装 GET POST 请求并导出
export default request;
