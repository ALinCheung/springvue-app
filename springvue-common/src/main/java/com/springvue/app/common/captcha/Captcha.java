package com.springvue.app.common.captcha;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 验证码实体
 */
@Schema(description = "验证码实体")
@Data
public class Captcha {

    @Schema(description = "唯一标识")
    private String uuid;

    @Schema(description = "验证码值")
    private String value;

    @Schema(description = "验证码图片, Mime Base64加密")
    private String image;

    @Schema(description = "过期时间, 毫秒")
    private Date expired;

    public Captcha(int timeout) {
        this.setExpired(new Date(System.currentTimeMillis() + timeout * 1000L));
    }
}
