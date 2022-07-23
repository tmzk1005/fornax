<template>
  <a-menu theme="dark" mode="inline">
    <a-menu-item key="dashboard" @click="goToPage(Paths.amDashboard)">
      <area-chart-outlined />
      <span>仪表板</span>
    </a-menu-item>
    <a-menu-item key="api" @click="goToPage(Paths.amApi)">
      <api-outlined />
      <span>API</span>
    </a-menu-item>
    <a-menu-item key="apiGroup" @click="goToPage(Paths.amApiGroup)">
      <group-outlined />
      <span>API分组</span>
    </a-menu-item>
    <a-menu-item key="app" @click="goToPage(Paths.amApp)">
      <appstore-outlined />
      <span>应用</span>
    </a-menu-item>

    <a-sub-menu key="settings">
      <template #title>
        <span>
          <setting-outlined />
          <span>系统设置</span>
        </span>
      </template>
      <a-menu-item key="settings-1">设置1</a-menu-item>
      <a-menu-item key="settings-2">设置2</a-menu-item>
      <a-menu-item key="settings-3">设置3</a-menu-item>
    </a-sub-menu>
  </a-menu>
</template>

<script setup>
import { AreaChartOutlined, ApiOutlined, GroupOutlined, SettingOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import { useRouter } from "vue-router"
import { Paths } from "@/utils/pathConstants"

const router = useRouter()

const goToPage = function (path) {
  console.log("goto ", path)
  router.push(path)
}
</script>
