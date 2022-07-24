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

      <a-row type="flex" justify="center">
        <a-col :span="24">
          <a-modal width="50%" v-model:visible="addUserModalVisible" title="新建用户">
            <a-form ref="newUserFormRef" :model="newUser" :rules="newUserFormRules" :label-col="{ span: 4 }"
              :wrapper-col="{ span: 24 }">
              <a-form-item label="用户名" name="username">
                <a-input v-model:value="newUser.username" placeholder="请输入用户名" />
              </a-form-item>
              <a-form-item label="昵称" name="nickname">
                <a-input v-model:value="newUser.nickname" placeholder="请输入昵称" />
              </a-form-item>
              <a-form-item label="密码" name="password">
                <a-input type="password" autocomplete="off" v-model:value="newUser.password" placeholder="请输入密码，至少需要8个字符" />
              </a-form-item>
              <a-form-item label="密码确认" name="passwordConfirm">
                <a-input type="password" autocomplete="off" v-model:value="newUser.passwordConfirm" placeholder="请确认密码" />
              </a-form-item>
              <a-form-item label="邮件" name="email">
                <a-input v-model:value="newUser.email" placeholder="请确认电子邮件" />
              </a-form-item>
              <a-form-item label="电话" name="phone">
                <a-input v-model:value="newUser.phone" placeholder="请确认电话号码" />
              </a-form-item>
            </a-form>
            <template #footer>
              <a-button key="submit" type="primary" @click="confirmAddUser">确认</a-button>
            </template>
          </a-modal>
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
import { PATTERN_NORMAL_NAME_ZH, PATTERN_NORMAL_NAME } from '@/utils/patternConstants'
import { notification } from "ant-design-vue"

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

const newUserFormRef = ref()

const newUser = reactive({ 
  username: null,
  nickname: null,
  password: null,
  passwordConfirm: null,
  email: null,
  phone: null,
})

const validatePasswordConfirm = async (_, value) => {
  if (value === '') {
    return Promise.reject('请输入确认密码')
  } else if (value !== newUser.password) {
    return Promise.reject("确认密码输入不一致!")
  } else {
    return Promise.resolve()
  }
}

const newUserFormRules = {
    username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 2, message: "用户名至少需要包含1个字符", trigger: "blur" },
    { max: 20, message: "用户名长度不能超过16个字符", trigger: "blur" },
    {
      pattern: PATTERN_NORMAL_NAME,
      message: "用户名只能包含字母,数字,下划线",
      trigger: "blur",
    },
  ],
  nickname: [
    { required: true, message: "请输入昵称", trigger: "blur" },
    { min: 2, message: "昵称至少需要包含1个字符", trigger: "blur" },
    { max: 20, message: "昵称长度不能超过16个字符", trigger: "blur" },
    {
      pattern: PATTERN_NORMAL_NAME_ZH,
      message: "昵称只能包含字母,数字,下划线,以及中文字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 8, message: "密码至少需要包含8个字符", trigger: "blur" },
    { max: 32, message: "密码长度不能超过32个字符", trigger: "blur" },
  ],
  passwordConfirm: [
    { validator: validatePasswordConfirm, trigger: 'change' }
  ],
  email: [
    { required: true, message: "请输入电子邮件", trigger: "blur" }
  ],
  phone: [
    { required: true, message: "请输入电话号码", trigger: "blur" }
  ],
}

const paginationConf = reactive({ ...DefaultPaginationConf })

const listUsers = (pageNum, pageSize) => {
  UserService.getUsers(pageNum, pageSize).then((data) => {
    userList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const confirmAddUser = () => {
  newUserFormRef.value.validate().then(() => {
    var userDto = {
      username: newUser.username,
      nickname: newUser.nickname,
      password: newUser.password,
      email: newUser.email,
      phone: newUser.phone
    }
    UserService.createUser(userDto).then(() => {
      notification.success({ message: "新建用户成功" })
      addUserModalVisible.value = false
      newUser.username = null
      newUser.nickname = null
      newUser.password1 = null
      newUser.password2 = null
      newUser.email = null
      newUser.phone = null
      listUsers(1, paginationConf.pageSize)
    })
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
