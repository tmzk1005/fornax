<template>
  <a-config-provider :locale="zh_CN">
    <div id="app">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script>
import zh_CN from "ant-design-vue/lib/locale/zh_CN"

export default {
  name: "App",
  data() {
    return {
      zh_CN,
    }
  },
}
</script>

<style>
#app {
  font-family: monospace, Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
