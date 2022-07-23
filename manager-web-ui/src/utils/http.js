import axios from "axios"
import router from "@/router"
import store from "@/store"
import { notification } from "ant-design-vue"
import { ApiPaths, Paths } from "./pathConstants"

const NoAuto = { autoHandle: false }

const configRequest = (config) => {
  if (config.data instanceof FormData) {
    Object.assign(config.headers, config.data.getHeaders())
  }
  return config
}

const handleStatus200Result = (resp) => {
  if (resp.config.autoHandle) {
    const respData = resp.data
    const code = respData.code
    if (code == undefined) {
      return respData
    } else if (code == 0) {
      return respData.data
    } else {
      const message = respData.message
      notification.error({
        message: message,
        duration: 2,
      })
      return Promise.reject(message)
    }
  } else {
    return resp.data
  }
}

const handleError = (error) => {
  console.error(error)
  var notificationMessage = "请求错误"
  var notificationDescription = "服务器内部错误，请联系管理员！"
  if (error.response.data && Object.hasOwn(error.response.data, 'message')) {
    notificationDescription = error.response.data.message
  }
  if (error.config.url == ApiPaths.getMe) {
    return Promise.reject(error)
  }
  notification.error({
    message: notificationMessage,
    duration: 2,
    description: notificationDescription
  })
  if (error.response.data && Object.hasOwn(error.response.data, 'code') && error.response.data.code == 401) {
    store.commit("clearUserInfo")
    setTimeout(() => {
      router.push(store.getters.isModeManagement ? Paths.apiManagementLoginPath : Paths.apiMarketLoginPath)
    }, 2000)
  }
  return Promise.reject(error)
}

const HttpClient = axios.create({
  baseURL: "/",
  headers: {
    "Content-Type": "application/json;charset=UTF-8;",
  },
  autoHandle: true
})
HttpClient.interceptors.request.use(configRequest, (error) => Promise.reject(error))
HttpClient.interceptors.response.use((resp) => handleStatus200Result(resp), (error) => handleError(error))

export { HttpClient, NoAuto }
