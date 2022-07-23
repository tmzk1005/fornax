<template>
  <a-layout style="padding: 0 24px 0 24px">

    <a-row type="flex" justify="center" style="margin-top: 1.5rem;">
      <a-col :span="24">
        <a-breadcrumb>
          <a-breadcrumb-item>API</a-breadcrumb-item>
        </a-breadcrumb>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-page-header title="API">
          <template v-if="store.getters.isNormalUser" #extra>
            <a-button type="primary" @click="onCreateApiClicked()">新建API</a-button>
          </template>
        </a-page-header>
      </a-col>
    </a-row>

    <a-row type="flex" justify="center">
      <a-col :span="24">
        <a-table :dataSource="apiList" :columns="apiFields" rowKey="id" :pagination="paginationConf" size="small"
          @change="tableChanged">
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'apiStatus'">
              <span>
                <a-tag :color="record.apiStatus == ApiStatus.online ? 'green' : 'red'">
                  {{ record.apiStatus == ApiStatus.online ? "已发布" : "已下线" }}
                </a-tag>
              </span>
            </template>
          </template>
          <template #action="{ record }">
            <a-space>
              <a-button v-if="store.getters.isNormalUser" type="link" style="padding: 0;" @click="changeApiStatus(record)">
                <template #icon>
                  <cloud-upload-outlined style="padding: 0; margin: 0;" />
                </template>
                {{ record.apiStatus == ApiStatus.online ? "下线" : "发布" }}
              </a-button>
              <a-button type="link" style="padding: 0;">
                <template #icon>
                  <exclamation-circle-outlined style="padding: 0; margin: 0;" />
                </template>
                详情
              </a-button>
              <a-button v-if="store.getters.isNormalUser" type="link" style="padding: 0;" @click="onDeleteApiClicked(record.id)">
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

  </a-layout>
</template>

<script setup>
import { ExclamationCircleOutlined, DeleteOutlined, CloudUploadOutlined } from '@ant-design/icons-vue'
import { ref, reactive } from 'vue'
import { DefaultPaginationConf } from '@/utils/dataStructure'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { notification } from 'ant-design-vue'

import { ApiService } from "@/api/apiService"
import { Paths } from '@/utils/pathConstants'

const store = useStore()

const ApiStatus = {
  online: "ONLINE",
  offline: "OFFLINE",
}

const apiList = ref([])

const apiFields = [
  {
    title: "API名称",
    dataIndex: "name",
  },
  {
    title: "版本",
    dataIndex: "version",
  },
  {
    title: "API所属分组",
    dataIndex: ["group", "name"]
  },
  {
    title: "请求路径",
    dataIndex: "path",
  },
  {
    title: "描述",
    dataIndex: "description",
  },
  {
    title: "发布状态",
    dataIndex: "apiStatus",
  },
  {
    title: "创建时间",
    dataIndex: "createdDate",
  },
  {
    title: "最后修改时间",
    dataIndex: "lastModifiedDate",
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" },
  },
]

const paginationConf = reactive({ ...DefaultPaginationConf })

const router = useRouter()

const listApis = (pageNum, pageSize) => {
  ApiService.getApis(pageNum, pageSize).then((data) => {
    apiList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const onCreateApiClicked = () => {
  router.push(Paths.amApiCreate)
}

const onDeleteApiClicked = (apiId) => {
  console.log(apiId)
}

const doOfflineApi = (record) => {
  ApiService.offlineApi(record.id).then(() => {
    notification.success({
      message: "下线API成功",
      duration: 2,
    })
    record.apiStatus = ApiStatus.offline
  })
}

const doPublishApi = (record) => {
  ApiService.publishApi(record.id).then(() => {
    notification.success({
      message: "发布API成功",
      duration: 2,
    })
    record.apiStatus = ApiStatus.online
  })
}

const changeApiStatus = (record) => {
  record.apiStatus == ApiStatus.online ? doOfflineApi(record) : doPublishApi(record)
}

const tableChanged = (changedPaginationConf) => {
  listApis(changedPaginationConf.current, changedPaginationConf.pageSize)
}

listApis(paginationConf.current, paginationConf.pageSize)

</script>