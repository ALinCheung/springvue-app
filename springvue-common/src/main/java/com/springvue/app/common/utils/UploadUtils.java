package com.springvue.app.common.utils;

import cn.hutool.core.io.FileUtil;
import com.springvue.app.common.model.UploadChunk;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 上传工具
 */
public class UploadUtils {

    /**
     * 分片上传
     *
     * @param params 分片文件入参
     * @param tempDir 临时名录
     * @param output 如果是目录则生成原始文件名, 否则生成指定文件名
     */
    public static File chunk(UploadChunk params, String tempDir, String output) throws Exception {
        String tempFileSuffix = ".finish";
        File tempPath = null;
        File outputFile = null;
        try {
            // 校验参数
            ValidateUtils.entity(params);
            if (StringUtils.isBlank(tempDir)) {
                throw new IllegalArgumentException("临时文件夹路径不能为空");
            }
            if (StringUtils.isBlank(output)) {
                throw new IllegalArgumentException("输出文件路径不能为空");
            }
            // 临时文件夹
            tempPath = FileUtil.file(tempDir + File.separator + params.getUuid());
            if (!FileUtil.exist(tempPath)) {
                FileUtil.mkdir(tempPath);
            }
            // 保存临时分片文件
            String tempFileName = params.getUuid() + "." + params.getIndex();
            File tempFile = FileUtil.file(tempPath.getAbsolutePath() + File.separator + tempFileName);
            try (InputStream is = params.getChunk().getInputStream()) {
                FileUtil.writeFromStream(is, tempFile);
                FileUtil.rename(tempFile, tempFileName + tempFileSuffix, true);
            }
            // 判断是否全部上传完成了
            File[] tempFiles = tempPath.listFiles((dir, name) -> name.endsWith(tempFileSuffix));
            if (tempFiles != null && tempFiles.length > 0 && tempFiles.length == params.getChunkCount()) {
                // 输出文件
                if (FileUtil.isDirectory(output)) {
                    outputFile = FileUtil.file(output + File.separator + params.getFileName());
                } else {
                    outputFile = FileUtil.file(output);
                }
                if (FileUtil.exist(outputFile)) {
                    FileUtil.del(outputFile);
                }
                // 合并文件
                try (BufferedOutputStream os = FileUtil.getOutputStream(outputFile)) {
                    Arrays.stream(tempFiles).sorted(new Comparator<File>() {
                        // 文件排序
                        @Override
                        public int compare(File o1, File o2) {
                            int i1 = Integer.parseInt(o1.getName().split("\\.")[1]);
                            int i2 = Integer.parseInt(o2.getName().split("\\.")[1]);
                            return Integer.compare(i1, i2);
                        }
                    }).forEach(o -> {
                        // 写入合并文件
                        FileUtil.writeToStream(o, os);
                    });
                    os.flush();
                    // 删除临时文件
                    FileUtil.del(tempPath);
                }
            }
            return outputFile;
        } catch (Exception e) {
            // 删除临时文件
            if (FileUtil.exist(tempPath)) {
                FileUtil.del(tempPath);
            }
            // 抛出异常
            throw e;
        }
    }
}
