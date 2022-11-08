package com.springvue.app.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "文件分片上传类")
@Data
public class UploadChunk {

    @ApiModelProperty(value = "上传分片文件唯一编码", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", required = true)
    @NotNull(message = "上传分片文件唯一编码不能为空")
    private String uuid;

    @ApiModelProperty(value = "上传文件名", example = "xxx.xlsx", required = true)
    @NotBlank(message = "上传文件名不能为空")
    private String fileName;

    @ApiModelProperty(value = "上传分片文件下标", example = "0", required = true)
    @NotNull(message = "上传分片文件下标不能为空")
    private Integer index;

    @ApiModelProperty(value = "上传分片文件数量", example = "1", required = true)
    @NotNull(message = "上传分片文件数量不能为空")
    private Integer chunkCount;

    @ApiModelProperty(value = "上传分片文件", required = true)
    @NotNull(message = "上传分片文件数量不能为空")
    private MultipartFile chunk;
}
