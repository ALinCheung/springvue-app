package com.springvue.app.common.ftp.config;

import lombok.Data;

/**
 * Ftp公共配置
 */
@Data
public class FtpConfig {

    private String host;

    private int port = 0;

    private String home;

    private String username;

    private String password;

    private int connectTimeOutMs = 15000;
}
