import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import { layout } from "common";

const staticRoutes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/HelloWorld",
    component: layout,
    children: [
      {
        path: "/HelloWorld",
        component: () => import("@/views/HelloWorld.vue"),
        meta: {
          title: "首页",
        },
      },
    ],
  },
];

const dynamicRoutes: Array<RouteRecordRaw> = [
  {
    path: "/uploadByPieces",
    component: layout,
    children: [
      {
        path: "",
        component: () => import("@/views/uploadByPieces.vue"),
        meta: {
          title: "分片上传",
        },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes: staticRoutes.concat(dynamicRoutes),
});

export default router;
