<template>
  <a-layout style="padding: 0 24px 0 24px;">

    <a-row type="flex" justify="center" style="margin-top: 1.5rem;">
      <a-col :span="24">
        <a-breadcrumb>
          <a-breadcrumb-item>API分组</a-breadcrumb-item>
        </a-breadcrumb>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-page-header title="API分组">
          <template v-if="store.getters.isNormalUser" #extra>
            <a-button type="primary" @click="() => addApiGroupModalVisible = true">新建API分组</a-button>
          </template>
        </a-page-header>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-table :dataSource="apiGroupList" :columns="apiGroupFields" rowKey="id" :pagination="paginationConf"
          size="small" @change="tableChanged">
          <template #action="{ record }">
            <a-space>
              <a-button type="link" style="padding: 0;">
                <template #icon>
                  <exclamation-circle-outlined style="padding: 0; margin: 0;" />
                </template>
                详情
              </a-button>
              <a-button v-if="store.getters.isNormalUser" type="link" style="padding: 0;" @click="onDeleteApiGroupClicked(record.id)">
                <template #icon>
                  <delete-outlined style="padding: 0; margin: 0;" />
                </template>
                删除
              </a-button>
            </a-space>
          </template>
        </a-table>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-modal width="50%" v-model:visible="addApiGroupModalVisible" title="新建API分组">
          <a-form ref="newApiGroupFormRef" :model="newApiGroup" :rules="newApiGroupFormRules" :label-col="{ span: 4 }"
            :wrapper-col="{ span: 24 }">
            <a-form-item label="分组名" name="name">
              <a-input v-model:value="newApiGroup.name" />
            </a-form-item>
            <a-form-item label="域名" name="address">
              <a-input v-model:value="newApiGroup.address" />
            </a-form-item>
            <a-form-item label="描述" name="description">
              <a-input v-model:value="newApiGroup.description" />
            </a-form-item>
          </a-form>
          <template #footer>
            <a-button key="submit" type="primary" @click="confirmAddApiGroup">确认</a-button>
          </template>
        </a-modal>
      </a-col>
    </a-row>

  </a-layout>
</template>

<script setup>
import { ExclamationCircleOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { DefaultPaginationConf } from '@/utils/dataStructure'
import { ApiGroupService } from "@/api/apiGroupService"
import { PATTERN_NORMAL_NAME_ZH, PATTERN_DOMAIN } from '@/utils/patternConstants'
import { notification } from "ant-design-vue"

const store = useStore()

const apiGroupList = ref([])

const addApiGroupModalVisible = ref(false)

const newApiGroupFormRef = ref()

const newApiGroup = reactive({ name: null, address: null, description: null })

const apiGroupFields = [
  {
    title: "名称",
    dataIndex: "name",
  },
  {
    title: "域名",
    dataIndex: "address",
  },
  {
    title: "说明",
    dataIndex: "description",
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" },
  },
]

const newApiGroupFormRules = {
  name: [
    { required: true, message: "请输入API分组名称", trigger: "blur" },
    {
      pattern: PATTERN_NORMAL_NAME_ZH,
      message: "API分组名称只能包含字母,数字,下划线,以及中文字符",
      trigger: "blur",
    },
  ],
  address: [
    { required: true, message: "请输入API分组域名", trigger: "blur" },
    {
      pattern: PATTERN_DOMAIN,
      message: "API分组域名非法,应该形如a1.b2.c3",
      trigger: "blur",
    },
  ],
}

const paginationConf = reactive({ ...DefaultPaginationConf })

const listApiGroups = (pageNum, pageSize) => {
  ApiGroupService.getApiGroups(pageNum, pageSize).then((data) => {
    apiGroupList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const confirmAddApiGroup = () => {
  newApiGroup.name = newApiGroup.name.trim()
  newApiGroup.address = newApiGroup.address.trim()
  newApiGroup.description = newApiGroup.description.trim()
  newApiGroupFormRef.value.validate().then(() => {
    ApiGroupService.createApiGroup(newApiGroup).then(() => {
      notification.success({ message: "新建API分组成功" })
      addApiGroupModalVisible.value = false
      newApiGroup.name = null
      newApiGroup.address = null
      newApiGroup.description = null
      listApiGroups(1, paginationConf.pageSize)
    })
  })
}

const onDeleteApiGroupClicked = (apiGroupId) => {
  console.log(apiGroupId)
}

const tableChanged = (changedPaginationConf) => {
  listApiGroups(changedPaginationConf.current, changedPaginationConf.pageSize)
}

listApiGroups(paginationConf.current, paginationConf.pageSize)

</script>