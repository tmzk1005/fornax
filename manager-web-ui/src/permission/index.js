import router from "@/router"
import store from "@/store"

import { Paths } from "@/utils/pathConstants"

import { UserService } from "@/api/userService"

const initPath = window.location.pathname

const tryFetchUserInfoIfPageRefeashed = function () {
  UserService.getSessionUser().then((userInfo) => {
    if (userInfo != null) {
      store.commit("setUserInfo", userInfo)
    }
    if (store.getters.isAuthenticated) {
      router.push({ path: initPath })
    }
  })
}

tryFetchUserInfoIfPageRefeashed()


const dicideWhereToGo = function (dstPath, loginPath, indexPath) {
  if (store.getters.isAuthenticated) {
    return dstPath == loginPath ? { path: indexPath } : true
  }
  return dstPath == loginPath ? true : { path: loginPath }
}

const goToApiManagementPage = function (dstPath) {
  store.commit('setModeManagement')
  return dicideWhereToGo(dstPath, Paths.apiManagementLoginPath, Paths.apiManagementIndexPath)
}

const goToApiMarketPage = function (dstPath) {
  store.commit('setModeMarket')
  return dicideWhereToGo(dstPath, Paths.apiMarketLoginPath, Paths.apiMarketIndexPath)
}

router.beforeEach(async (to) => {
  console.log("before go to", to.path)
  const dstPath = to.path
  if (dstPath.startsWith(Paths.apiManagementIndexPath) || dstPath == Paths.apiManagementLoginPath) {
    return goToApiManagementPage(dstPath)
  } else if (dstPath.startsWith(Paths.apiMarketIndexPath) || dstPath == Paths.apiMarketLoginPath) {
    return goToApiMarketPage(dstPath)
  } else if (dstPath == "/") {
    return true
  }
  return { path: "/" }
})
