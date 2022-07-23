<template>
  <div>
    <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiBackendInfo">
      <a-form-item label="后端服务类型" name="backendType">
        <a-radio-group v-model:value="apiBackendInfo.backendType" button-style="solid">
          <a-radio-button value="HTTP">HTTP/HTTPS</a-radio-button>
          <a-radio-button value="MOCK">MOCK</a-radio-button>
        </a-radio-group>
      </a-form-item>
    </a-form>

    <a-divider style="margin-top: 20px;" />

    <div style="margin-top: 20px;" v-show="apiBackendInfo.backendType == 'HTTP'">
      <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiBackendInfo.httpBackend"
        ref="httpBackendFormRef" :rules="httpBackendFormRules">
        <a-form-item label="协议" name="httpProtocol">
          <a-radio-group v-model:value="apiBackendInfo.httpBackend.httpProtocol" button-style="solid">
            <a-radio-button value="HTTP">HTTP</a-radio-button>
            <a-radio-button value="HTTPS">HTTPS</a-radio-button>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="Http方法" name="httpMethod">
          <a-select v-model:value="apiBackendInfo.httpBackend.httpMethod">
            <a-select-option value="GET">GET</a-select-option>
            <a-select-option value="POST">POST</a-select-option>
            <a-select-option value="PUT">PUT</a-select-option>
            <a-select-option value="DELETE">DELETE</a-select-option>
            <a-select-option value="HEAD">HEAD</a-select-option>
            <a-select-option value="PATCH">PATCH</a-select-option>
            <a-select-option value="OPTIONS">OPTIONS</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="后端服务地址" name="address">
          <a-input v-model:value="apiBackendInfo.httpBackend.address" />
        </a-form-item>

        <a-form-item label="后端服务请求路径" name="path">
          <a-input v-model:value="apiBackendInfo.httpBackend.path" />
        </a-form-item>

        <a-form-item label="后端超时(毫秒)" name="timeoutSeconds">
          <a-input v-model:value="apiBackendInfo.httpBackend.timeoutSeconds" />
        </a-form-item>
      </a-form>
    </div>

    <div style="margin-top: 20px;" v-show="apiBackendInfo.backendType == 'MOCK'">
      <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiBackendInfo.mockBackend"
        ref="mockBackendFormRef" :rules="mockBackendFormRules">
        <a-form-item label="响应状态码" name="statusCode">
          <a-input v-model:value="apiBackendInfo.mockBackend.statusCode" />
        </a-form-item>
        <a-form-item label="MOCK响应内容" name="body">
          <a-textarea v-model:value="apiBackendInfo.mockBackend.body" placeholder="请输入正常的MOCK响应" :rows="8" />
        </a-form-item>
      </a-form>
    </div>
  </div>

</template>

<script setup>
import { reactive, ref, defineExpose } from 'vue'

const apiBackendInfo = reactive({
  backendType: "HTTP",
  httpBackend: {
    httpProtocol: "HTTP",
    httpMethod: "GET",
    address: "",
    path: "/",
    timeoutSeconds: 0,
  },
  mockBackend: {
    statusCode: 200,
    headers: {},
    body: ""
  }
})

const httpBackendFormRef = ref()

const httpBackendFormRules = {
  protocol: [{ required: true, message: "协议不能为空", trigger: "blur" }],
  httpMethod: [{ required: true, message: "请求方法不能为空", trigger: "blur" }],
  address: [{ required: true, message: "后端服务地址不能为空", trigger: "blur" }],
  path: [{ required: true, message: "后端服务请求路径不能为空", trigger: "blur" }],
  timeoutSeconds: [{ required: true, type: 'number', message: "超时时间不能为空, 请输入0表示不需要控制超时", trigger: "blur" }],
}

const mockBackendFormRef = ref()

const mockBackendFormRules = {
  statusCode: [{ required: true, type: 'number', message: "必须设置响应状态码", trigger: "blur" }],
  body: [{ required: true, message: "Mock响应体不能为空", trigger: "blur" }],
}

const checkAndReturnInputData = () => {
  if (apiBackendInfo.backendType == 'HTTP') {
    return httpBackendFormRef.value.validate().then(() => {
      return apiBackendInfo
    })
  } else if (apiBackendInfo.backendType == 'MOCK') {
    return mockBackendFormRef.value.validate().then(() => {
      return apiBackendInfo
    })
  }
}

defineExpose({ checkAndReturnInputData })
</script>