package com.springvue.app.server.controller;

import com.springvue.app.common.model.Result;
import com.springvue.app.common.model.UploadChunk;
import com.springvue.app.common.utils.UploadUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.File;

@Tag(name = "上传模块")
@RestController
public class UploadController {

    public static String tempDir = "D:\\temp";
    public static String outputDir = "D:\\temp";

    @Parameter(name = "params", description = "上传入参")
    @Operation(summary = "分片上传")
    @PostMapping("/uploadByPieces")
    public Result uploadByPieces(@Valid UploadChunk params) throws Exception {
        File file = UploadUtils.chunk(params, tempDir, outputDir);
        if (file != null) {
            return Result.success();
        }
        return Result.process();
    }
}
