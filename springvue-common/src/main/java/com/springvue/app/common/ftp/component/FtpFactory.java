package com.springvue.app.common.ftp.component;

import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.session.FtpSession;
import com.springvue.app.common.ftp.util.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.util.Map;

/**
 * Ftp连接池工厂类
 */
@Slf4j
public class FtpFactory extends BaseKeyedPooledObjectFactory<String, FtpSession> {

    private final Map<String, FtpConfig> configMap;

    public FtpFactory(Map<String, FtpConfig> configMap) {
        this.configMap = configMap;
        if (configMap == null) {
            throw new NullPointerException("configMap is null");
        }
    }

    private FtpConfig load(String key) {
        FtpConfig ftpConfig = configMap.get(key);
        if (ftpConfig == null) {
            throw new NullPointerException("key " + key + " not found in configMap");
        }
        return ftpConfig;
    }

    @Override
    public FtpSession create(String key) throws Exception {
        FtpConfig ftpConfig = load(key);
        FtpSession session = FtpUtils.createSession(ftpConfig);
        log.debug("create session[{}:{}]", ftpConfig.getHost(), ftpConfig.getPort());
        return session;
    }

    @Override
    public PooledObject<FtpSession> wrap(FtpSession ftpSession) {
        return new DefaultPooledObject<>(ftpSession);
    }

    @Override
    public void destroyObject(String key, PooledObject<FtpSession> p) {
        FtpSession session = p.getObject();
        FtpConfig config = session.getConfig();
        session.close();
        log.debug("closed session[{}:{}]", config.getHost(), config.getPort());
    }

    @Override
    public boolean validateObject(String key, PooledObject<FtpSession> p) {
        FtpSession session = p.getObject();
        FtpConfig config = session.getConfig();
        boolean available = session.isAvailable();
        log.debug("checked keyed session[{}:{}] , available: [{}] ", config.getHost(), config.getPort(), available);
        return available;
    }

    @Override
    public void passivateObject(String key, PooledObject<FtpSession> p) throws Exception {
        FtpSession session = p.getObject();
        FtpConfig config = session.getConfig();
        if (!session.home()) {
            throw new IOException(String.format("切换主目录失败[%s]", config.getHome()));
        } else {
            log.debug("changed keyed session[{}:{}] current dir to home[{}]", config.getHost(), config.getPort(), config.getHome());
        }
    }
}
