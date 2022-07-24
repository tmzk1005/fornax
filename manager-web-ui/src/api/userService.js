import router from "@/router"
import store from "@/store"
import { HttpClient, NoAuto } from "@/utils/http"
import { ApiPaths, Paths } from "@/utils/pathConstants"

const getLoginPath = () => {
  return store.getters.isModeManagement ? Paths.apiManagementLoginPath : Paths.apiMarketLoginPath
}

const login = (userInfo) => {
  return HttpClient.post(ApiPaths.login, userInfo, NoAuto).then(
    (data) => {
      if (data.code == 0) {
        return data.data
      } else {
        router.push(getLoginPath())
      }
    }
  )
}

const logout = () => {
  return HttpClient.post(ApiPaths.logout, null, NoAuto).then(
    (data) => {
      if (data.code == 0) {
        store.commit("clearUserInfo")
        router.push(getLoginPath())
      }
    }
  )
}

const getSessionUser = () => {
  const userInfo = {
    id: null,
    username: null,
    nickname: "未登录"
  }
  return HttpClient.get(ApiPaths.getMe, NoAuto).then((data) => {
    if (data.code == 0) {
      return data.data
    }
    return userInfo
  }).catch(() => {
    return userInfo
  })
}

const getUsers = (pageNum, pageSize) => {
  const params = { pageNum: pageNum, pageSize: pageSize }
  return HttpClient.get(ApiPaths.user, { params: params })
}

const createUser = (userDto) => {
  return HttpClient.post(ApiPaths.user, userDto)
}

const UserService = {
  login: login,
  logout: logout,
  getSessionUser: getSessionUser,
  getUsers: getUsers,
  createUser: createUser,
}

export { UserService }
