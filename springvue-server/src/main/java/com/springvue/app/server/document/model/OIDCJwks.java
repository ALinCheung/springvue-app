package com.springvue.app.server.document.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "OIDC JWK信息")
@Data
public class OIDCJwks {

    @Schema(description = "加密信息数组")
    private List<OIDCJwksKey> keys;

    @Schema(description = "加密信息")
    @Data
    class OIDCJwksKey {

        @Schema(description = "标识与密钥一起使用的加密算法族，如\"RSA\"或\"EC\"")
        private String kty;

        @Schema(description = "RSA Key 的公共指数")
        private String e;

        @Schema(description = "标识公钥的预期用途。\"use\"参数用于指示是否使用公钥加密数据或验证数据上的签名。")
        private String use;

        @Schema(description = "用于匹配特定密钥。")
        private String kid;

        @Schema(description = "标识要与密钥一起使用的算法")
        private String alg;

        @Schema(description = "令牌类型")
        private String n;
    }
}
