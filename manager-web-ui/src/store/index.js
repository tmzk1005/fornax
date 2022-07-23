import { createStore } from "vuex"

const MODE_MANAGEMNET = "management"
const MODE_MARKET = "market"

const ROLES = {
  systemAdmin: 'SYSTEM_ADMIN',
  securityAdmin: 'SECURITY_ADMIN',
  auditAdmin: 'AUDIT_ADMIN',
  normalUser: 'NORMAL_USER',
}

export default createStore({
  state() {
    return {
      siteMode: MODE_MANAGEMNET,
      userInfo: {
        id: null,
        username: null,
        nickname: "未登录",
        role: null,
      },
    }
  },
  getters: {
    isAuthenticated: (state) => {
      return state.userInfo.username != null
    },
    isModeManagement: (state) => {
      return state.siteMode == MODE_MANAGEMNET
    },
    isModeMarket: (state) => {
      return state.siteMode == MODE_MARKET
    },
    isSystemAdmin: (state) => {
      return state.userInfo.role == ROLES.systemAdmin
    },
    isSecurityAdmin: (state) => {
      return state.userInfo.role == ROLES.securityAdmin
    },
    isAuditAdmin: (state) => {
      return state.userInfo.role == ROLES.auditAdmin
    },
    isNormalUser: (state) => {
      return state.userInfo.role == ROLES.normalUser
    },
  },
  mutations: {
    setUserInfo(state, userInfo) {
      state.userInfo.id = userInfo.id
      state.userInfo.username = userInfo.username
      state.userInfo.nickname = userInfo.nickname
      state.userInfo.role = userInfo.role
    },
    clearUserInfo(state) {
      state.userInfo.id = null
      state.userInfo.username = null
      state.userInfo.nickname = null
      state.userInfo.role = null
    },
    setModeManagement(state) {
      state.siteMode = MODE_MANAGEMNET
    },
    setModeMarket(state) {
      state.siteMode = MODE_MARKET
    }
  },
  actions: {},
  modules: {},
})
