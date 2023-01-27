<template>
  <button @click="onShow">开始验证</button>
  <Vcode :show="isShow" @success="onSuccess" @close="onClose" />
</template>

<script setup>
import { ref } from "vue";
import Vcode from "vue3-puzzle-vcode";

const isShow = ref(false);

const onShow = () => {
  isShow.value = true;
};

const onClose = () => {
  isShow.value = false;
};

const onSuccess = () => {
  onClose(); // 验证成功，需要手动关闭模态框
};
</script>
