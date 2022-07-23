<template>
  <a-layout style="padding: 0 24px 0 24px">
    <a-row type="flex" justify="center" style="margin-top: 1.5rem;">
      <a-col :span="24">
        <a-breadcrumb>
          <a-breadcrumb-item href="#" @click="router.push(Paths.amApi)">API</a-breadcrumb-item>
          <a-breadcrumb-item>新建API</a-breadcrumb-item>
        </a-breadcrumb>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-page-header title="新建API" />
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="22">
        <a-steps type="navigation" size="small" :current="currentStep">
          <a-step title="基本信息" />
          <a-step title="请求信息" />
          <a-step title="后端服务" />
          <a-step title="可选配置" />
          <a-step title="确认" />
        </a-steps>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center" style="padding-top: 30px">
      <a-col :span="22">
        <api-create-step1 ref="step1Ref" v-show="currentStep == 0" />
        <api-create-step2 ref="step2Ref" v-show="currentStep == 1" />
        <api-create-step3 ref="step3Ref" v-show="currentStep == 2" />
        <api-create-step4 ref="step4Ref" v-show="currentStep == 3" />
        <api-create-step5 ref="step5Ref" v-show="currentStep == 4" v-bind:apiDtoJsonString="apiDtoJsonString.content" />
      </a-col>
    </a-row>
  </a-layout>

  <div style="position: fixed; width: 100%; bottom: 0; padding: 6px 0 8px 0; background-color: #CAE1FF;">
    <a-row>
      <a-col :offset="16" :span="2">
        <a-button style="padding-right: 30px" :disabled="currentStep == 0" @click="onPreStepClicked">上一步</a-button>
      </a-col>
      <a-col :span="2">
        <a-button type="primary" style="padding-right: 30px" @click="onNextStepClicked">
          {{ nextStepBottonText }}
        </a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import ApiCreateStep1 from "@/components/apiCreate/ApiCreateStep1.vue"
import ApiCreateStep2 from "@/components/apiCreate/ApiCreateStep2.vue"
import ApiCreateStep3 from "@/components/apiCreate/ApiCreateStep3.vue"
import ApiCreateStep4 from "@/components/apiCreate/ApiCreateStep4.vue"
import ApiCreateStep5 from "@/components/apiCreate/ApiCreateStep5.vue"
import { ref, reactive } from 'vue'
import { useRouter } from "vue-router"
import { notification } from "ant-design-vue"
import { ApiService } from "@/api/apiService"
import { Paths } from "@/utils/pathConstants"

const currentStep = ref(0)
const step1Ref = ref()
const step2Ref = ref()
const step3Ref = ref()
const step4Ref = ref()
const step5Ref = ref()
const nextStepBottonText = ref("下一步")

var apiBaseInfo = null
var apiRequestInfo = null
var apiBackendInfo = null
var apiOptionalInfo = null

var apiDto = {}
const apiDtoJsonString = reactive({ content: "" })

const onPreStepClicked = () => {
  currentStep.value -= 1
  nextStepBottonText.value = "下一步"
}

const checkStep1 = () => {
  step1Ref.value.checkAndReturnInputData().then(data => {
    apiBaseInfo = data
    currentStep.value += 1
    nextStepBottonText.value = "下一步"
  })
}

const checkStep2 = () => {
  step2Ref.value.checkAndReturnInputData().then(data => {
    apiRequestInfo = data
    currentStep.value += 1
    nextStepBottonText.value = "下一步"
  })
}

const checkStep3 = () => {
  step3Ref.value.checkAndReturnInputData().then(data => {
    apiBackendInfo = data
    currentStep.value += 1
    nextStepBottonText.value = "下一步"
  })
}

const checkStep4 = () => {
  step4Ref.value.checkAndReturnInputData().then(data => {
    apiOptionalInfo = data
    currentStep.value += 1
    nextStepBottonText.value = "完成"
    apiDto = { ...apiBaseInfo, ...apiRequestInfo, ...apiBackendInfo, ...apiOptionalInfo }
    apiDtoJsonString.content = JSON.stringify(apiDto, null, 4)
  })
}

const router = useRouter()

const checkStep5 = () => {
  ApiService.createApi(apiDto).then(() => {
    notification.success({ message: "新建API成功" })
    router.push(Paths.amApi)
  })
}

const onNextStepClicked = () => {
  switch (currentStep.value) {
    case 0:
      checkStep1()
      break
    case 1:
      checkStep2()
      break
    case 2:
      checkStep3()
      break
    case 3:
      checkStep4()
      break
    case 4:
      checkStep5()
      break
  }
}

</script>