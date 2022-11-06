import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import { layout } from "common";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/HelloWorld",
    component: layout,
    children: [
      {
        path: "/HelloWorld",
        component: () => import("@/components/HelloWorld.vue"),
        meta: {
          title: "首页",
        },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
