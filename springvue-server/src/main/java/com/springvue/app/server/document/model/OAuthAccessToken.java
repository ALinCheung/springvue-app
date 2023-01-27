package com.springvue.app.server.document.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "OAuth访问令牌")
@Data
public class OAuthAccessToken {

    @ApiModelProperty(value = "访问令牌")
    private String access_token;

    @ApiModelProperty(value = "令牌类型", example = "bearer")
    private String token_type;

    @ApiModelProperty(value = "刷新令牌, 用于刷新access_token")
    private String refresh_token;

    @ApiModelProperty(value = "有效秒数", example = "3600")
    private Integer expires_in;

    @ApiModelProperty(value = "权限范围", example = "read write")
    private String scope;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "许可证")
    private String license;

    @ApiModelProperty(value = "域")
    private String domain;
}
