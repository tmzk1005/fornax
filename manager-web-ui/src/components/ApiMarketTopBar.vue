<template>
  <a-menu mode="horizontal" theme="light" v-model:selectedKeys="noUse">
    <a-menu-item @click="goToPage(Paths.apiMarketIndexPath)">
      <template #icon>
        <cloud-server-outlined />
      </template>
      <span style="font-weight: bold;">Fornax-API市场</span>
    </a-menu-item>

    <a-sub-menu style="margin-left: auto">
      <template #icon>
        <user-outlined />
      </template>
      <template #title>{{ curLoginedUser.nickname }}</template>
      <a-menu-item @click="goToPage(Paths.apiManagementIndexPath)">
        <template #icon>
          <gateway-outlined />
        </template>
        API管理平台
      </a-menu-item>
      <a-menu-item>
        <template #icon>
          <info-circle-outlined />
        </template>
        个人信息
      </a-menu-item>
      <a-menu-item>
        <template #icon>
          <edit-outlined />
        </template>
        修改密码
      </a-menu-item>
      <a-menu-item>
        <template #icon>
          <setting-outlined />
        </template>
        设置
      </a-menu-item>
      <a-menu-item @click="onLogout">
        <template #icon>
          <logout-outlined />
        </template>
        退出
      </a-menu-item>
    </a-sub-menu>
  </a-menu>
</template>

<script setup>
import { computed, reactive } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import { Paths } from "@/utils/pathConstants"
import { UserService } from "@/api/userService"

import {
  CloudServerOutlined, UserOutlined, GatewayOutlined,
  InfoCircleOutlined, SettingOutlined, LogoutOutlined, EditOutlined
} from "@ant-design/icons-vue"

const store = useStore()
const router = useRouter()

const noUse = reactive([""])

const curLoginedUser = computed(() => {
  return { username: store.state.userInfo.username, nickname: store.state.userInfo.nickname }
})

const onLogout = function () {
  const usernameBackup = curLoginedUser.value.username
  UserService.logout().then((succeed) => {
    if (succeed) {
      console.log("%s logout succeed", usernameBackup)
      router.push(Paths.apiMarketLoginPath)
    } else {
      console.log("%s logout failed", curLoginedUser.value.username)
    }
  })
}

const goToPage = function (path) {
  console.log("goto ", path)
  router.push(path)
}
</script>
