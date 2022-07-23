<template>
  <div>
    <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiOptionalInfo" ref="formRef">
      <a-form-item label="认证方式" name="type">
        <a-radio-group v-model:value="apiOptionalInfo.authenticationType" button-style="solid">
          <a-radio-button value="NONE">无认证</a-radio-button>
          <a-radio-button value="APP">APP</a-radio-button>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import { defineExpose, reactive, ref } from 'vue'

const formRef = ref()
const apiOptionalInfo = reactive({
  authenticationType: "NONE"
})

const checkAndReturnInputData = () => {
  return formRef.value.validate().then(() => {
    return apiOptionalInfo
  })
}

defineExpose({ checkAndReturnInputData })
</script>