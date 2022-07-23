import { HttpClient } from "@/utils/http"
import { ApiPaths } from "@/utils/pathConstants"

const getApiGroups = (pageNum, pageSize) => {
  const params = { pageNum: pageNum, pageSize: pageSize }
  return HttpClient.get(ApiPaths.amApiGroup, { params: params })
}

const createApiGroup = (apiGroupDto) => {
  return HttpClient.post(ApiPaths.amApiGroup, apiGroupDto)
}

const searchApiGroup = (text) => {
  const params = { text: text }
  return HttpClient.get(ApiPaths.amApiGroupSearch, { params: params })
}

const ApiGroupService = {
  getApiGroups, createApiGroup, searchApiGroup
}

export { ApiGroupService }
