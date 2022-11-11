package com.springvue.app.common.ftp.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SFtp配置实现类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SFtpConfigImpl extends FtpConfig {

    private String controlEncoding;
}
