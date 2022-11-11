package com.springvue.app.common.ftp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("ftp")
@Data
public class FtpProperties {
    private List<FtpServerProperties> servers;
    private FtpPoolProperties pool;
}