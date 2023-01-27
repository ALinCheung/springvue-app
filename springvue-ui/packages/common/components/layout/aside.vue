<template>
  <el-menu default-active="0" class="el-menu-vertical-demo">
    <el-menu-item
      v-for="menu in menus"
      :key="menu.path"
      :index="getIndex(menus, menu, 'path').toString()"
      @click="handleMenu(menu)"
    >
      <el-icon><icon-menu /></el-icon>
      <span>{{ menu.title }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">
import { Menu as IconMenu } from "@element-plus/icons-vue";
import { Router, useRouter } from "vue-router";
import { MenuItem } from "/common/model/menu";
import { getIndex } from "../../utils";
const menus: Array<MenuItem> = [
  {
    path: "/",
    title: "首页",
  },
  {
    path: "/uploadByPieces",
    title: "分片上传",
  },
  {
    path: "/puzzleVcode",
    title: "验证码",
  },
];
const router = useRouter() as Router;
const handleMenu = (menuItem: MenuItem) => {
  router.push(menuItem.path);
};
</script>

<style scoped></style>
