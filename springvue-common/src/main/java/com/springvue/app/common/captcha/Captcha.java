package com.springvue.app.common.captcha;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 验证码实体
 */
@ApiModel(description = "验证码实体")
@Data
public class Captcha {

    @ApiModelProperty(value = "唯一标识")
    private String uuid;

    @ApiModelProperty(value = "验证码值")
    private String value;

    @ApiModelProperty(value = "验证码图片, Mime Base64加密")
    private String image;

    @ApiModelProperty(value = "过期时间, 毫秒")
    private Date expired;

    public Captcha(int timeout) {
        this.setExpired(new Date(System.currentTimeMillis() + timeout * 1000L));
    }
}
