package com.springvue.app.server.controller;

import com.springvue.app.server.model.UploadChunk;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "上传模块")
@RestController
public class UploadController {

    @ApiImplicitParam(name = "params", value = "上传入参")
    @ApiOperation(value = "分片上传")
    @PostMapping("/uploadByPieces")
    public ResponseEntity<String> uploadByPieces(UploadChunk params) {
        return ResponseEntity.ok(null);
    }
}
