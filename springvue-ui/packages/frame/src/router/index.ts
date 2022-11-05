import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/HelloWorld",
    children: [
      {
        path: "/HelloWorld",
        component: () => import("@/components/HelloWorld.vue"),
        meta: {
          title: "HelloWorld",
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
