<template>
  <div v-if="store.getters.isSystemAdmin">
    <a-layout style="padding: 0 24px 0 24px">

      <a-row type="flex" justify="center" style="margin-top: 1.5rem;">
        <a-col :span="24">
          <a-breadcrumb>
            <a-breadcrumb-item>用户管理</a-breadcrumb-item>
          </a-breadcrumb>
        </a-col>
      </a-row>

      <a-row type="flex" justify="center">
        <a-col :span="24">
          <a-page-header title="用户">
            <template #extra>
              <a-button type="primary" @click="() => addUserModalVisible = true">新建用户</a-button>
            </template>
          </a-page-header>
        </a-col>
      </a-row>

      <a-row type="flex" justify="center">
        <a-col :span="24">
          <a-table :dataSource="userList" :columns="userFields" rowKey="id" :pagination="paginationConf" size="small" @change="tableChanged">
            <template #action="{ record }">
              <a-space>
                <a-button type="link" style="padding: 0;">
                  <template #icon>
                    <exclamation-circle-outlined style="padding: 0; margin: 0;" />
                  </template>
                  详情
                </a-button>
                <a-button type="link" style="padding: 0;" @click="onDeleteUserClicked(record.id)">
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
  </div>

</template>

<script setup>
import { ExclamationCircleOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { UserService } from "@/api/userService"
import { DefaultPaginationConf } from '@/utils/dataStructure'

const store = useStore()

const addUserModalVisible = ref(false)
const userList = ref([])

const userFields = [
  {
    title: "用户名",
    dataIndex: "username",
  },
  {
    title: "昵称",
    dataIndex: "nickname",
  },
  {
    title: "角色",
    dataIndex: "role",
  },
  {
    title: "邮件",
    dataIndex: "email",
  },
  {
    title: "电话",
    dataIndex: "phone",
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" },
  },
]

const paginationConf = reactive({ ...DefaultPaginationConf })

const listUsers = (pageNum, pageSize) => {
  UserService.getUsers(pageNum, pageSize).then((data) => {
    userList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const onDeleteUserClicked = (userId) => {
  console.log("TODO : delete user by id" + userId)
}

const tableChanged = (changedPaginationConf) => {
  listUsers(changedPaginationConf.current, changedPaginationConf.pageSize)
}

listUsers(paginationConf.current, paginationConf.pageSize)

</script>
