<template>
  <el-config-provider :locale="locale">
    <router-view :routes="routes"></router-view>
  </el-config-provider>
</template>

<script setup lang="ts">
import zhCn from "element-plus/dist/locale/zh-cn.mjs";
import { ElConfigProvider } from "element-plus";
import { useRouter } from "vue-router";

const locale = zhCn;
const routes = useRouter().getRoutes();
</script>

<style scoped></style>
