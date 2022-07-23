<template>
  <a-layout style="min-height: 100vh;">
    <a-layout-header class="header" style="padding: 0; position: fixed; zIndex: 1; width: 100%;">
      <api-market-top-bar></api-market-top-bar>
    </a-layout-header>
    <a-layout style="padding: 0; margin: 64px 0 0 0;">
      <router-view />
    </a-layout>
  </a-layout>
</template>

<script setup>
import ApiMarketTopBar from '@/components/ApiMarketTopBar.vue'
</script>
