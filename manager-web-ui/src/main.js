import { createApp } from "vue"
import App from "@/views/App.vue"
import router from "@/router"
import store from "@/store"

import "ant-design-vue/dist/antd.css"
import Antd from "ant-design-vue/es"
import 'highlight.js/styles/github.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from '@highlightjs/vue-plugin'

import "@/permission"

createApp(App).use(store).use(router).use(Antd).use(hljsVuePlugin).mount("#app")
