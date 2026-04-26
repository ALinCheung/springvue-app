package com.springvue.app.server.document.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "OAuth访问令牌")
@Data
public class OAuthCheckToken {

    @Schema(description = "许可证")
    private String license;

    @Schema(description = "资源列表")
    private List<String> aud;

    @Schema(description = "用户名")
    private String user_name;

    @Schema(description = "权限列表")
    private List<String> scope;

    @Schema(description = "域")
    private String domain;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "是否存活")
    private Boolean active;

    @Schema(description = "到期秒数")
    private Integer exp;

    @Schema(description = "角色列表")
    private List<String> authorities;

    @Schema(description = "客户端编码")
    private String client_id;
}
