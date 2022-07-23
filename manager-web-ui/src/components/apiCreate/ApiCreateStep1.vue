<template>
  <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 8 }" :model="apiBaseInfo" ref="formRef" :rules="formRules">
    <a-form-item label="名称" name="name">
      <a-input v-model:value="apiBaseInfo.name" />
    </a-form-item>

    <a-form-item label="版本" name="version">
      <a-input v-model:value="apiBaseInfo.version" />
    </a-form-item>

    <a-form-item label="分组" name="apiGroupId">
      <a-select v-model:value="apiBaseInfo.apiGroupId" :show-arrow="true" :allowClear="true"
        :options="apiGroupSelectOptions" placeholder="选择API所属分组">
      </a-select>
    </a-form-item>

    <a-form-item label="描述" name="description" v-model:value="apiBaseInfo.description">
      <a-textarea v-model:value="apiBaseInfo.description" />
    </a-form-item>
  </a-form>
</template>

<script setup>
import { reactive, ref, defineExpose } from 'vue'
import { PATTERN_NORMAL_NAME_ZH, PATTERN_VERSION } from '@/utils/patternConstants'
import { ApiGroupService } from '@/api/apiGroupService'

const apiBaseInfo = reactive({
  name: "",
  version: "0.0.0",
  apiGroupId: "",
  description: "",
})

const formRef = ref()

const formRules = {
  name: [
    { required: true, message: "请输入API名称", trigger: "blur" },
    { min: 4, message: "API名称至少需要包含4个字符", trigger: "blur" },
    { max: 20, message: "API名称长度不能超过20个字符", trigger: "blur" },
    { pattern: PATTERN_NORMAL_NAME_ZH, message: "API名称只能包含字母,数字,下划线,以及中文字符", trigger: "blur" },
  ],
  version: [
    { required: true, message: "请输入API版本", trigger: "blur" },
    { pattern: PATTERN_VERSION, message: "版本号不符合1.2.3的形式", trigger: "blur" },
  ],
  apiGroupId: [{ required: true, message: "API所属分组不能为空", trigger: "blur" }]
}

const apiGroupSelectOptions = ref([])

const fetchApiGroups = (text) => {
  ApiGroupService.searchApiGroup(text).then((data) => {
    apiGroupSelectOptions.value = data.map((item) => ({ label: item.name, value: item.id }))
  })
}

const checkAndReturnInputData = () => {
  return formRef.value.validate().then(() => {
    return apiBaseInfo
  })
}

fetchApiGroups("")

defineExpose({ checkAndReturnInputData })
</script>
