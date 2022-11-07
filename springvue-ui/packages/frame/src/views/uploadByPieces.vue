<template>
  <el-upload
    ref="uploadRef"
    action=""
    :auto-upload="false"
    :http-request="upload"
  >
    <template #trigger>
      <el-button type="primary">select file</el-button>
    </template>

    <el-button class="ml-3" type="success" @click="submitUpload">
      upload to server
    </el-button>
  </el-upload>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import type { UploadInstance } from "element-plus";
import { UploadRequestOptions } from "element-plus/es/components/upload/src/upload";
import { uploadByPieces } from "common";

const uploadRef = ref<UploadInstance>();

const upload = (options: UploadRequestOptions) => {
  console.log(options.file);
  uploadByPieces("/api/uploadByPieces", options);
};

const submitUpload = () => {
  uploadRef.value?.submit();
};
</script>

<style scoped>
.ml-3 {
  margin-left: 0.75rem;
}
</style>
