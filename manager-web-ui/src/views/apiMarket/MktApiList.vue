<template>
  <a-row style="padding: 30px 0 40px 0; margin: 0">
    <a-col :offset="3" :span="18">
      <a-input-search v-model:value="searchText" placeholder="搜索API" size="large" enter-button />
    </a-col>
  </a-row>
  <a-row :gutter="[16, 16]" :wrap="true" style="padding: 0 40px 0 40px; margin: 0">
    <a-col :span="6" v-for="apiRelease in apiReleaseList" :key="apiRelease.id">
      <a-card :title="apiRelease.publishHistory.apiHistory.name" type="inner" :headStyle="{ fontWeight: 'bold' }">
        <p>Card content</p>
        <p>Card content</p>
        <p>Card content</p>
      </a-card>
    </a-col>
  </a-row>
  <a-row :gutter="[16, 16]" style="padding: 30px 40px 0 40px; margin: 0" justify="end">
    <a-col>
      <a-pagination :showQuickJumper="paginationConf.showQuickJumper" :showSizeChanger="paginationConf.showSizeChanger"
        :total="paginationConf.total" :current="paginationConf.current" :pageSize="paginationConf.pageSize"
        :pageSizeOptions="paginationConf.pageSizeOptions" :showTotal="paginationConf.showTotal" @change="pageChanged" />
    </a-col>
  </a-row>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ApiService } from '@/api/apiService'

import { DefaultPaginationConf } from '@/utils/dataStructure'

const apiReleaseList = ref([])

const searchText = ref('')

const paginationConf = reactive({ ...DefaultPaginationConf })
paginationConf.pageSize = 8
paginationConf.pageSizeOptions = ['8', '12', '16', '20', '24', '28']

const listApiReleases = (pageNum, pageSize) => {
  ApiService.getApiReleases(pageNum, pageSize).then((data) => {
    apiReleaseList.value = data.data
    paginationConf.total = data.total
    paginationConf.current = data.pageNum
    paginationConf.pageSize = data.pageSize
  })
}

const pageChanged = (pageNum, pageSize) => {
  listApiReleases(pageNum, pageSize)
}

listApiReleases(paginationConf.current, paginationConf.pageSize)
</script>