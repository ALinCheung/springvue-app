package com.springvue.app.server.document.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "OIDC JWK信息")
@Data
public class OIDCJwks {

    @ApiModelProperty(value = "加密信息数组")
    private List<OIDCJwksKey> keys;

    @ApiModel(description = "加密信息")
    @Data
    class OIDCJwksKey {

        @ApiModelProperty(value = "标识与密钥一起使用的加密算法族，如“RSA”或“EC”")
        private String kty;

        @ApiModelProperty(value = "RSA Key 的公共指数")
        private String e;

        @ApiModelProperty(value = "标识公钥的预期用途。“use”参数用于指示是否使用公钥加密数据或验证数据上的签名。")
        private String use;

        @ApiModelProperty(value = "用于匹配特定密钥。")
        private String kid;

        @ApiModelProperty(value = "标识要与密钥一起使用的算法")
        private String alg;

        @ApiModelProperty(value = "令牌类型")
        private String n;
    }
}
