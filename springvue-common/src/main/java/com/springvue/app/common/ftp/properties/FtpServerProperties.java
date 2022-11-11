package com.springvue.app.common.ftp.properties;

import com.springvue.app.common.ftp.model.FileType;
import com.springvue.app.common.ftp.model.ProtocolType;
import com.springvue.app.common.ftp.model.SystemType;
import com.springvue.app.common.ftp.util.FtpUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ftp.servers")
@Data
public class FtpServerProperties {

    private String key;
    private ProtocolType protocolType;
    private String host;
    private int port;
    private String home;
    private String username;
    private String password;
    private String controlEncoding;
    private FileType fileType;
    private int connectTimeOutMs;
    private int dataTimeout;
    private boolean passiveMode;
    private SystemType systemKey;
    private String serverLanguageCode;
    private String defaultDateFormatStr;
    private String recentDateFormatStr;
    private String serverTimeZoneId;
    private boolean remoteVerificationEnabled;

    public FtpServerProperties() {
        this.key = FtpUtils.DEFAULT_KEY;
        this.protocolType = ProtocolType.FTP;
        this.port = 21;
        this.username = "ftp";
        this.password = "ftp";
        this.controlEncoding = "GBK";
        this.fileType = FileType.BINARY_FILE_TYPE;
        this.connectTimeOutMs = 15000;
        this.dataTimeout = 0;
        this.passiveMode = true;
        this.systemKey = SystemType.UNIX;
        this.remoteVerificationEnabled = true;
    }
}

