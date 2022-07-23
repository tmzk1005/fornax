import { HttpClient } from "@/utils/http"
import { ApiPaths } from "@/utils/pathConstants"

const getApis = (pageNum, pageSize) => {
  const params = { pageNum: pageNum, pageSize: pageSize }
  return HttpClient.get(ApiPaths.amApi, { params: params })
}

const createApi = (apiDto) => {
  return HttpClient.post(ApiPaths.amApi, apiDto)
}

const publishApi = (apiId) => {
  return HttpClient.post(`${ApiPaths.amApiPublish}/${apiId}`)
}

const offlineApi = (apiId) => {
  return HttpClient.post(`${ApiPaths.amApiOffline}/${apiId}`)
}

const ApiService = {
  getApis, publishApi, offlineApi, createApi
}

export { ApiService }
