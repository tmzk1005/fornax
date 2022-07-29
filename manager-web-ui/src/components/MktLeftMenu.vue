<template>
  <a-menu theme="light" mode="inline">
    <a-menu-item key="apiMarket" @click="goToPage(Paths.mktApiList)">
      <area-chart-outlined />
      <span>API市场</span>
    </a-menu-item>
    <a-menu-item key="app" v-if="store.getters.isNormalUser" @click="goToPage(Paths.mktMyApp)">
      <appstore-outlined />
      <span>我的应用</span>
    </a-menu-item>
  </a-menu>
</template>

<script setup>
import { AreaChartOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import { useRouter } from "vue-router"
import { Paths } from "@/utils/pathConstants"
import { useStore } from 'vuex'

const store = useStore()
const router = useRouter()

const goToPage = function (path) {
  console.log("goto ", path)
  router.push(path)
}
</script>
