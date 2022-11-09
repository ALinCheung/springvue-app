package com.springvue.app.common.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

/**
 * 针对非session的验证码服务
 * session的验证码用kaptcha.render()
 */
@Data
public abstract class CaptchaStore {

    /**
     * 验证码有效期，单位为秒，默认3分钟
     */
    protected Integer timeout = 3;

    /**
     * 验证码生成工具
     */
    protected DefaultKaptcha kaptcha;

    public CaptchaStore(DefaultKaptcha kaptcha) {
        this.kaptcha = kaptcha;
    }

    public CaptchaStore(DefaultKaptcha kaptcha, Integer timeout) {
        this.kaptcha = kaptcha;
        this.timeout = timeout;
    }

    /**
     * 生成验证码
     */
    public Captcha generate() throws Exception {
        Captcha captcha = new Captcha(timeout);
        // 唯一编码
        captcha.setUuid(UUID.randomUUID().toString());
        // 生成验证码
        captcha.setValue(kaptcha.createText());
        // 生成图片
        BufferedImage image = kaptcha.createImage(captcha.getValue());
        // 转换成base64串, 删除 \r\n
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        Base64.Encoder encoder = Base64.getMimeEncoder();
        String base64Image = encoder.encodeToString(bytes).trim().replaceAll("[\n\r]", "");
        captcha.setImage(base64Image);
        return captcha;
    }

    /**
     * 验证
     */
    protected abstract Boolean verify(String uuid, String captcha);
}
