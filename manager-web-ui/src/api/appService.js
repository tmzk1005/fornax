import { HttpClient } from "@/utils/http"
import { ApiPaths } from "@/utils/pathConstants"

const getApps = (pageNum, pageSize) => {
  const params = { pageNum: pageNum, pageSize: pageSize }
  return HttpClient.get(ApiPaths.amApp, { params: params })
}

const createApp = (appDto) => {
  return HttpClient.post(ApiPaths.amApp, appDto)
}

const AppService = {
  getApps: getApps,
  createApp: createApp,
}

export { AppService }
