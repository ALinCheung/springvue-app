package com.springvue.app.server.document.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "OAuth访问令牌")
@Data
public class OAuthCheckToken {

    @ApiModelProperty(value = "许可证")
    private String license;

    @ApiModelProperty(value = "资源列表")
    private List<String> aud;

    @ApiModelProperty(value = "用户名")
    private String user_name;

    @ApiModelProperty(value = "权限列表")
    private List<String> scope;

    @ApiModelProperty(value = "域")
    private String domain;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "是否存活")
    private Boolean active;

    @ApiModelProperty(value = "到期秒数")
    private Integer exp;

    @ApiModelProperty(value = "角色列表")
    private List<String> authorities;

    @ApiModelProperty(value = "客户端编码")
    private String client_id;
}
