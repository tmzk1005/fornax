<template>
  <a-row style="margin-top: 150px">
    <a-col :offset="14" :span="6">
      <span style="font-size: 2rem; float: right">登录-Grace-API市场</span>
    </a-col>
  </a-row>
  <a-row>
    <a-col :offset="14" :span="6" style="margin-top: 30px">
      <a-form v-model="userInfo" :label-col="{ span: 4 }" :wrapper-col="{ span: 24 }">
        <a-form-item ref="username" label="用户名" name="username">
          <a-input v-model:value="userInfo.username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item has-feedback label="密码" name="password">
          <a-input
            v-model:value="userInfo.password"
            type="password"
            autocomplete="off"
            placeholder="请输入密码"
          />
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 24, offset: 4 }">
          <a-typography-text :type="loginStatus.color">{{ loginStatus.msg }}</a-typography-text>
          <br />
          <div style="margin-top: 10px">
            <a-button type="primary" html-type="submit" @click="onLogin">登录</a-button>
            <a-button style="margin-left: 10px" @click="onReset">重置</a-button>
          </div>
        </a-form-item>
      </a-form>
    </a-col>
  </a-row>
</template>

<script setup>
import { reactive } from "vue"
import { UserService } from '@/api/userService'
import { useStore } from "vuex"
import { useRouter } from "vue-router"

import { Paths } from "@/utils/pathConstants"

const store = useStore()
const router = useRouter()

const userInfo = reactive({
  username: "",
  password: "",
})

const loginStatus = reactive({ msg: "", color: "secondary" })

const onLogin = function () {
  loginStatus.msg = "正在登录..."
  loginStatus.color = "secondary"
  UserService.login(userInfo).then((loginedUserInfo) => {
    if (loginedUserInfo == null) {
      loginStatus.msg = "用户名或者密码错误"
      loginStatus.color = "danger"
      console.log("%s login failed", userInfo.username)
    } else {
      loginStatus.msg = "登录成功"
      loginStatus.color = "success"
      console.log("logined user: username=%s, nickname=%s", loginedUserInfo.username, loginedUserInfo.nickname)
      store.commit("setUserInfo", loginedUserInfo)
      router.push(Paths.apiMarketIndexPath)
      console.log("%s login succeed", loginedUserInfo.username)
    }
  })
}

const onReset = function () {
  userInfo.username = ""
  userInfo.password = ""
  loginStatus.msg = ""
}

</script>
