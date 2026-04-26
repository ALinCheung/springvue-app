package com.springvue.app.server.document.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "OIDC用户信息")
@Data
public class OIDCUserInfo {

    @Schema(description = "身份标识")
    private String sub;

    @Schema(description = "令牌")
    private OIDCAccessToken accessToken;

    @Schema(description = "OIDC访问令牌")
    @Data
    class OIDCAccessToken {

        @Schema(description = "令牌值")
        private String tokenValue;

        @Schema(description = "创建秒数")
        private String issuedAt;

        @Schema(description = "过期秒数")
        private String expiresAt;

        @Schema(description = "令牌类型")
        private String tokenType;

        @Schema(description = "OAuth范围")
        private List<String> scopes;
    }
}
