import { createRouter, createWebHistory } from "vue-router"

import { Paths } from "@/utils/pathConstants"

const apiManagementRoutes = {
  path: Paths.apiManagementIndexPath,
  redirect: Paths.amDashboard,
  component: () => import("@/views/apiManagement/AmIndex.vue"),
  children: [
    {
      path: Paths.amApi,
      component: () => import("@/views/apiManagement/AmApi.vue")
    },
    {
      path: Paths.amApiCreate,
      component: () => import("@/views/apiManagement/AmApiCreate.vue")
    },
    {
      path: Paths.amApiGroup,
      component: () => import("@/views/apiManagement/AmApiGroup.vue")
    },
    {
      path: Paths.amApp,
      component: () => import("@/views/apiManagement/AmApp.vue")
    },
    {
      path: Paths.amDashboard,
      component: () => import("@/views/apiManagement/AmDashboard.vue")
    }
  ]
}

const apiMarketRoutes = {
  path: Paths.apiMarketIndexPath,
  component: () => import("@/views/apiMarket/MktIndex.vue"),
  redirect: Paths.mktApiList,
  children: [
    {
      path: Paths.mktApiList,
      component: () => import("@/views/apiMarket/MktApiList.vue")
    },
  ]
}

const routes = [
  {
    path: Paths.apiManagementLoginPath,
    component: () => import("@/views/apiManagement/AmLogin.vue")
  },
  {
    path: Paths.apiMarketLoginPath,
    component: () => import("@/views/apiMarket/MktLogin.vue"),
  },
  {
    path: "/",
    component: () => import("@/views/TheHome.vue"),
    redirect: Paths.apiManagementIndexPath,
    children: [
      apiManagementRoutes,
      apiMarketRoutes
    ]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: routes
})

export default router
