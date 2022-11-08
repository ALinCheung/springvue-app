package com.springvue.app.server.controller;

import com.springvue.app.common.model.Result;
import com.springvue.app.common.model.UploadChunk;
import com.springvue.app.common.utils.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;

@Api(tags = "上传模块")
@RestController
public class UploadController {

    public static String tempDir = "D:\\temp";
    public static String outputDir = "D:\\temp";

    @ApiImplicitParam(name = "params", value = "上传入参")
    @ApiOperation(value = "分片上传")
    @PostMapping("/uploadByPieces")
    public Result uploadByPieces(@Valid UploadChunk params) throws Exception {
        File file = UploadUtils.chunk(params, tempDir, outputDir);
        if (file != null) {
            return Result.success();
        }
        return Result.process();
    }
}
