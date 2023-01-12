package com.springvue.app.server.document.model;

import com.agile.sso.common.model.Oauth2UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "OIDC用户信息")
@Data
public class OIDCUserInfo {

    @ApiModelProperty(value = "身份标识")
    private String sub;

    @ApiModelProperty(value = "令牌")
    private OIDCAccessToken accessToken;

    @ApiModelProperty(value = "用户信息")
    private Oauth2UserInfo userinfo;

    @ApiModel(description = "OIDC访问令牌")
    @Data
    class OIDCAccessToken {

        @ApiModelProperty(value = "令牌值")
        private String tokenValue;

        @ApiModelProperty(value = "创建秒数")
        private String issuedAt;

        @ApiModelProperty(value = "过期秒数")
        private String expiresAt;

        @ApiModelProperty(value = "令牌类型")
        private String tokenType;

        @ApiModelProperty(value = "OAuth范围")
        private List<String> scopes;
    }
}
