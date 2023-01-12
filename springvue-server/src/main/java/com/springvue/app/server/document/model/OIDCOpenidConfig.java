package com.springvue.app.server.document.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "OIDC配置信息")
@Data
public class OIDCOpenidConfig {

    @ApiModelProperty(value = "授权服务器的颁发者标识符的URL")
    private String issuer;

    @ApiModelProperty(value = "授权端点的URL")
    private String authorization_endpoint;

    @ApiModelProperty(value = "令牌端点的URL")
    private String token_endpoint;

    @ApiModelProperty(value = "令牌端点支持的客户端身份验证方法")
    private List<String> token_endpoint_auth_methods_supported;

    @ApiModelProperty(value = "解析jwk为json的URL")
    private String jwks_uri;

    @ApiModelProperty(value = "用户信息端点")
    private String userinfo_endpoint;

    @ApiModelProperty(value = "支持的响应类型")
    private List<String> response_types_supported;

    @ApiModelProperty(value = "支持的授权类型")
    private List<String> grant_types_supported;

    @ApiModelProperty(value = "取消令牌端点的URL")
    private String introspection_endpoint;

    @ApiModelProperty(value = "取消令牌端点支持的客户端身份验证方法")
    private List<String> introspection_endpoint_auth_methods_supported;

    @ApiModelProperty(value = "支持对象类型")
    private List<String> subject_types_supported;

    @ApiModelProperty(value = "id_token的支持加密类型")
    private List<String> id_token_signing_alg_values_supported;

    @ApiModelProperty(value = "支持使用的范围")
    private List<String> scopes_supported;
}
