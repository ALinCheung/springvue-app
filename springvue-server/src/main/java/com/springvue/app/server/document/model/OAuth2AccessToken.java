package com.springvue.app.server.document.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "OAuth2访问令牌")
@Data
public class OAuth2AccessToken {

    @ApiModelProperty(value = "访问令牌")
    private String access_token;

    @ApiModelProperty(value = "令牌类型", example = "bearer")
    private String token_type;

    @ApiModelProperty(value = "刷新令牌, 用于刷新access_token")
    private String refresh_token;

    @ApiModelProperty(value = "有效秒数", example = "3600")
    private Integer expires_in;

    @ApiModelProperty(value = "权限范围", example = "read write openid")
    private String scope;

    @ApiModelProperty(value = "jwt的身份令牌")
    private String id_token;
}
