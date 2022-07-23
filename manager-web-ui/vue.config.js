const { defineConfig } = require("@vue/cli-service")
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: "/",
  devServer: {
    proxy: "http://127.0.0.1:9000",
  },
  chainWebpack: (config) => {
    config.plugin("html").tap((args) => {
      args[0].title = "Fornax-API网关管理平台"
      return args
    })
  },
  configureWebpack: {
    devtool: "eval-source-map",
  },
})
