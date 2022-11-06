<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <layout-header></layout-header>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <layout-aside :routes="routes"></layout-aside>
        </el-aside>
        <el-container>
          <el-main>
            <layout-main></layout-main>
          </el-main>
          <el-footer> <layout-footer></layout-footer></el-footer>
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
