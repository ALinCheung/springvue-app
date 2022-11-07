<template>
  <div class="common-layout">
    <el-container>
      <el-header class="common-layout-header">
        <layout-header></layout-header>
      </el-header>
      <el-container class="common-layout-container">
        <el-aside class="common-layout-aside">
          <layout-aside :routes="routes"></layout-aside>
        </el-aside>
        <el-container>
          <el-main class="common-layout-main">
            <layout-main></layout-main>
          </el-main>
          <el-footer class="common-layout-footer">
            <layout-footer></layout-footer
          ></el-footer>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import layoutHeader from "./header.vue";
import layoutAside from "./aside.vue";
import layoutMain from "./main.vue";
import layoutFooter from "./footer.vue";
import { defineProps, PropType } from "vue";
import { RouteRecord } from "vue-router";

const props = defineProps({
  routes: Object as PropType<RouteRecord>,
});
</script>

<style scoped>
.common-layout {
  height: 100vh;
}
.common-layout-container {
  height: calc(100vh - 60px);
}
.common-layout-header {
  border-bottom: 1px solid rgba(60, 60, 60, 0.12);
}
.common-layout-aside {
  width: 200px;
  border-right: 1px solid rgba(60, 60, 60, 0.12);
}
.common-layout-main {
  padding: 0;
}
.common-layout-footer {
  border-top: 1px solid rgba(60, 60, 60, 0.12);
}
</style>
