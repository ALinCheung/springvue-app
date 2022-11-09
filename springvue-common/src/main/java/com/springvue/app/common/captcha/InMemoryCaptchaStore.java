package com.springvue.app.common.captcha;


import com.google.code.kaptcha.impl.DefaultKaptcha;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存验证码服务
 */
public class InMemoryCaptchaStore extends CaptchaStore {

    /**
     * 定时器
     */
    private final Timer timer = new Timer(true);

    /**
     * 验证码存储结构
     */
    private final ConcurrentHashMap<String, Captcha> captchaMap = new ConcurrentHashMap<String, Captcha>();


    public InMemoryCaptchaStore(DefaultKaptcha kaptcha) {
        super(kaptcha);
        this.schedule();
    }

    public InMemoryCaptchaStore(DefaultKaptcha kaptcha, Integer timeout) {
        super(kaptcha, timeout);
        this.schedule();
    }

    /**
     * 每分钟执行一次
     */
    private void schedule() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verifyExpired();
            }
        }, 60 * 1000, 60 * 1000);
    }

    @Override
    public Captcha generate() throws Exception {
        Captcha captcha = super.generate();
        captchaMap.putIfAbsent(captcha.getUuid(), captcha);
        return captcha;
    }

    @Override
    public Boolean verify(String uuid, String captcha) {
        Captcha Captcha = captchaMap.get(uuid);
        if (Captcha == null || this.verifyExpired(Captcha)) {
            return false;
        }
        return Captcha.getValue().toLowerCase().equals(captcha.toLowerCase());
    }

    /**
     * 验证失效的验证码
     */
    private void verifyExpired() {
        for (Map.Entry<String, Captcha> entry : captchaMap.entrySet()) {
            String uuid = entry.getKey();
            Captcha captcha = entry.getValue();
            if (this.verifyExpired(captcha)) {
                // 已过期，清除对应验证码
                captchaMap.remove(uuid);
            }
        }
    }

    /**
     * 验证失效的验证码
     */
    private Boolean verifyExpired(Captcha captcha) {
        Date now = new Date();
        // 当前时间大于过期时间
        return now.compareTo(captcha.getExpired()) > 0;
    }
}
