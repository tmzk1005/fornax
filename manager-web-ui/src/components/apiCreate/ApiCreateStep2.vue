<template>
  <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiRequestInfo" ref="formRef" :rules="formRules">
    <a-form-item label="Http方法" name="httpMethods">
      <a-select mode="multiple" v-model:value="apiRequestInfo.httpMethods">
        <a-select-option value="GET">GET</a-select-option>
        <a-select-option value="POST">POST</a-select-option>
        <a-select-option value="PUT">PUT</a-select-option>
        <a-select-option value="DELETE">DELETE</a-select-option>
        <a-select-option value="HEAD">HEAD</a-select-option>
        <a-select-option value="PATCH">PATCH</a-select-option>
        <a-select-option value="OPTIONS">OPTIONS</a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="请求路径" name="path">
      <a-input v-model:value="apiRequestInfo.path" />
    </a-form-item>

  </a-form>
</template>

<script setup>
import { reactive, ref, defineExpose } from 'vue'

const formRef = ref()
const apiRequestInfo = reactive({
  httpMethods: [],
  path: "/",
})

const formRules = {
  httpMethods: [
    { required: true, message: "必须设置HTTP请求方法", trigger: "blur" },
  ],
  path: [
    { required: true, message: "必须设置HTTP请求路径", trigger: "blur" },
  ],
}

const checkAndReturnInputData = () => {
  return formRef.value.validate().then(() => {
    return apiRequestInfo
  })
}

defineExpose({ checkAndReturnInputData })
</script>