<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header class="header" style="padding: 0; position: fixed; zIndex: 1; width: 100%;">
      <api-management-top-bar></api-management-top-bar>
    </a-layout-header>
    <a-layout style="margin: 64px 0 0 0; padding: 0">
      <a-layout-sider v-model:collapsed="collapsed" collapsible
        style="position: fixed; height: 100vh; overflow: auto; padding: 0; margin: 0">
        <api-management-left-menu></api-management-left-menu>
      </a-layout-sider>
      <a-layout-content :style="{ marginLeft: mainContentMarginLeft }">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import ApiManagementTopBar from '@/components/ApiManagementTopBar.vue'
import ApiManagementLeftMenu from '@/components/ApiManagementLeftMenu.vue'

import { ref, computed } from 'vue'

const collapsed = ref(false)

const mainContentMarginLeft = computed(() => collapsed.value ? "80px" : "200px")

</script>
