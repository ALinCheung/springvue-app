package com.springvue.app.common.ftp.config;

import com.springvue.app.common.ftp.model.FileType;
import com.springvue.app.common.ftp.model.SystemType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ftp配置实现类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FtpConfigImpl extends FtpConfig {

    private FileType fileType = FileType.BINARY_FILE_TYPE;

    private String controlEncoding = "GBK";

    private int dataTimeout = 0;

    private boolean passiveMode = true;

    private SystemType systemKey = SystemType.UNIX;

    private String serverLanguageCode;

    private String defaultDateFormatStr;

    private String recentDateFormatStr;

    private String serverTimeZoneId;

    private boolean remoteVerificationEnabled = true;
}
