<template>
  <el-upload
    ref="uploadRef"
    action=""
    :auto-upload="false"
    :http-request="uploadRequest"
  >
    <template #trigger>
      <el-button type="primary">select file</el-button>
    </template>

    <el-button class="ml-3" type="success" @click="submitUpload">
      upload to server
    </el-button>
  </el-upload>

  <el-dialog
    v-model="dialog.visible"
    :title="dialog.title"
    width="30%"
    :before-close="handleClose"
  >
    <el-progress
      v-if="dialog.uploadStatus === 'processing'"
      :text-inside="true"
      :stroke-width="24"
      :percentage="dialog.percentage"
      :status="dialog.percentageStatus"
    />
    <span v-if="dialog.uploadStatus !== 'processing'">{{
      dialog.uploadStatusText
    }}</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, reactive } from "vue";
import { ElMessageBox } from "element-plus";
import type { UploadInstance } from "element-plus";
import { UploadRequestOptions } from "element-plus/es/components/upload/src/upload";
import { upload } from "@/api/upload";

const uploadRef = ref<UploadInstance>();
const dialog = reactive({
  visible: true,
  title: "上传中...",
  uploadStatus: "processing",
  uploadStatusText: "上传中",
  percentage: 0,
  percentageStatus: "",
});

const submitUpload = () => {
  uploadRef.value?.submit();
};
const uploadRequest = (options: UploadRequestOptions) => {
  dialog.visible = true;
  dialog.title = "上传中...";
  dialog.uploadStatus = "processing";
  dialog.uploadStatusText = "上传中";
  dialog.percentage = 0;
  dialog.percentageStatus = "";
  upload(
    {
      data1: "data1",
      data2: "data2",
    },
    options.file,
    (loaded) => {
      dialog.percentage = loaded;
      if (loaded === 100) {
        dialog.percentageStatus = "success";
      }
    }
  )
    .then((res) => {
      dialog.title = "提示";
      dialog.uploadStatus = "success";
      dialog.uploadStatusText = "上传成功";
      console.log(res);
    })
    .catch((err) => {
      dialog.title = "提示";
      dialog.uploadStatus = "fail";
      dialog.uploadStatusText = "上传失败, 原因: " + err;
    });
};

const handleClose = () => {
  if (dialog.uploadStatus !== "processing") {
    dialog.visible = false;
  } else {
    ElMessageBox.confirm("确定要取消上传吗?", "提示")
      .then(() => {
        dialog.visible = false;
      })
      .catch((err) => {
        ElMessageBox.alert(err);
      });
  }
};
</script>

<style scoped>
.ml-3 {
  margin-left: 0.75rem;
}
</style>
