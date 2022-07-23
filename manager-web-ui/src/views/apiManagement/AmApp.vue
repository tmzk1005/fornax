<template>
  <a-layout style="padding: 0 24px 0 24px">

    <a-row type="flex" justify="center" style="margin-top: 1.5rem;">
      <a-col :span="24">
        <a-breadcrumb>
          <a-breadcrumb-item>应用</a-breadcrumb-item>
        </a-breadcrumb>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-page-header title="应用">
          <template v-if="store.getters.isNormalUser" #extra>
            <a-button type="primary" @click="() => addAppModalVisible = true">新建应用</a-button>
          </template>
        </a-page-header>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-table :dataSource="appList" :columns="appFields" rowKey="id" :pagination="paginationConf" size="small"
          @change="tableChanged">
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'key'">
              <pre style="margin: 0"><code>{{ record.key }}</code></pre>
            </template>
            <template v-if="column.dataIndex === 'secret'">
              <pre style="margin: 0"><code>{{ record.secret }}</code></pre>
            </template>
          </template>
          <template #action="{ record }">
            <a-space>
              <a-button type="link" style="padding: 0;">
                <template #icon>
                  <exclamation-circle-outlined style="padding: 0; margin: 0;" />
                </template>
                详情
              </a-button>
              <a-button v-if="store.getters.isNormalUser" type="link" style="padding: 0;" @click="onDeleteAppClicked(record.id)">
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
        <a-modal width="50%" v-model:visible="addAppModalVisible" title="新建应用">
          <a-form ref="newAppFormRef" :model="newApp" :rules="newAppFormRules" :label-col="{ span: 4 }"
            :wrapper-col="{ span: 24 }">
            <a-form-item label="应用名称" name="name">
              <a-input v-model:value="newApp.name" />
            </a-form-item>
            <a-form-item label="描述" name="description">
              <a-input v-model:value="newApp.description" />
            </a-form-item>
          </a-form>
          <template #footer>
            <a-button key="submit" type="primary" @click="confirmAddApp">确认</a-button>
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
import { PATTERN_NORMAL_NAME_ZH } from '@/utils/patternConstants'
import { notification } from 'ant-design-vue'
import { AppService } from "@/api/appService"

const store = useStore()

const appList = ref([])
const appFields = [
  {
    title: "应用名称",
    dataIndex: "name",
  },
  {
    title: "应用Key",
    dataIndex: "key",
  },
  {
    title: "应用Secret",
    dataIndex: "secret",
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" },
  },
]

const addAppModalVisible = ref(false)
const newAppFormRef = ref()
const newApp = reactive({ name: null, description: null })

const newAppFormRules = {
  name: [
    { required: true, message: "请输入API分组名称", trigger: "blur" },
    {
      pattern: PATTERN_NORMAL_NAME_ZH,
      message: "应用分组名称只能包含字母,数字,下划线,以及中文字符",
      trigger: "blur",
    },
  ]
}

const paginationConf = reactive({ ...DefaultPaginationConf })

const confirmAddApp = () => {
  newApp.name = newApp.name.trim()
  newApp.description = newApp.description.trim()
  newAppFormRef.value.validate().then(() => {
    AppService.createApp(newApp).then(() => {
      notification.success({ message: "新建应用成功" })
      addAppModalVisible.value = false
      newApp.name = null
      newApp.description = null
      listApps(1, paginationConf.pageSize)
    })
  })
}

const onDeleteAppClicked = (appId) => {
  console.log(appId)
}

const listApps = (pageNum, pageSize) => {
  AppService.getApps(pageNum, pageSize).then((data) => {
    appList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const tableChanged = (changedPaginationConf) => {
  listApps(changedPaginationConf.current, changedPaginationConf.pageSize)
}

listApps(paginationConf.current, paginationConf.pageSize)

</script>
