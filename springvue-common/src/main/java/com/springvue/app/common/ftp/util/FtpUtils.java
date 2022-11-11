package com.springvue.app.common.ftp.util;

import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.session.FtpSession;
import com.springvue.app.common.ftp.model.ProtocolType;
import com.springvue.app.common.ftp.properties.FtpServerProperties;
import com.springvue.app.common.ftp.config.FtpConfigImpl;
import com.springvue.app.common.ftp.session.FtpSessionImpl;
import com.springvue.app.common.ftp.config.SFtpConfigImpl;
import com.springvue.app.common.ftp.session.SFtpSessionImpl;
import org.springframework.beans.BeanUtils;

import java.net.URI;
import java.util.Date;

/**
 * Ftp工具类
 */
public class FtpUtils {

    public final static String DEFAULT_KEY = "single";

    /**
     * 配置转化
     */
    public static FtpConfig toConfig(FtpServerProperties server) {
        if (server.getProtocolType() == ProtocolType.FTP) {
            FtpConfigImpl ftpConfigImpl = new FtpConfigImpl();
            BeanUtils.copyProperties(server, ftpConfigImpl);
            return ftpConfigImpl;
        } else if (server.getProtocolType() == ProtocolType.SFTP) {
            SFtpConfigImpl SFtpConfigImpl = new SFtpConfigImpl();
            BeanUtils.copyProperties(server, SFtpConfigImpl);
            return SFtpConfigImpl;
        } else {
            throw new IllegalArgumentException("未知的协议类型:" + server.getProtocolType());
        }
    }

    /**
     * 创建会话
     */
    public static FtpSession createSession(FtpConfig ftpConfig) throws Exception {
        FtpSession session;
        if (ftpConfig instanceof FtpConfigImpl) {
            session = new FtpSessionImpl((FtpConfigImpl) ftpConfig);
            session.connect();
        } else if (ftpConfig instanceof SFtpConfigImpl) {
            session = new SFtpSessionImpl((SFtpConfigImpl) ftpConfig);
            session.connect();
        } else {
            throw new IllegalArgumentException("不支持的文件服务器协议配置类型:" + ftpConfig.getClass().getName());
        }
        return session;
    }

    /**
     * 构建地址
     */
    public static String buildUrl(FtpConfig ftpConfig) throws Exception {
        String schema;
        if (ftpConfig instanceof SFtpConfigImpl) {
            schema = "sftp";
        } else if (ftpConfig instanceof FtpConfigImpl) {
            schema = "ftp";
        } else {
            throw new IllegalArgumentException("未知的协议:" + ftpConfig.getClass().getName());
        }
        URI url = new URI(schema, ftpConfig.getUsername() + ":" + ftpConfig.getPassword()
                , ftpConfig.getHost(), ftpConfig.getPort(), null,
                null, null);
        return url.toString();
    }

    /**
     * 比较日期
     */
    public static boolean compare(Date fileDate, Date dataStartTime, Date dataEndTime) {
        boolean isSelected = false;

        if (dataStartTime == null && dataEndTime == null) {
            isSelected = true;
        } else if (dataStartTime != null && dataEndTime == null) {
            if (fileDate.after(dataStartTime)) {
                isSelected = true;
            }
        } else if (dataStartTime == null) {
            if (fileDate.before(dataEndTime)) {
                isSelected = true;
            }
        } else {
            isSelected = (fileDate.after(dataStartTime) && fileDate.before(dataEndTime))
                    || fileDate.compareTo(dataStartTime) == 0;
        }
        return isSelected;
    }

}
