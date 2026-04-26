package com.springvue.app.server.document.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "OAuth2访问令牌")
@Data
public class OAuth2AccessToken {

    @Schema(description = "访问令牌")
    private String access_token;

    @Schema(description = "令牌类型", example = "bearer")
    private String token_type;

    @Schema(description = "刷新令牌, 用于刷新access_token")
    private String refresh_token;

    @Schema(description = "有效秒数", example = "3600")
    private Integer expires_in;

    @Schema(description = "权限范围", example = "read write openid")
    private String scope;

    @Schema(description = "jwt的身份令牌")
    private String id_token;
}
