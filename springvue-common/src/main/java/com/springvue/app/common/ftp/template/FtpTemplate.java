package com.springvue.app.common.ftp.template;

import com.springvue.app.common.ftp.model.RemoteFile;
import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.session.FtpSession;
import com.springvue.app.common.ftp.util.FtpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.KeyedObjectPool;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Ftp调用模板
 */
public class FtpTemplate {

    private KeyedObjectPool<String, FtpSession> ftpPool;

    public FtpTemplate(KeyedObjectPool<String, FtpSession> ftpPool) {
        this.ftpPool = ftpPool;
    }

    public <T> T execute(String key, FtpCallBack<T> callBack) throws Exception {
        FtpSession session = null;
        try {
            // 默认FTP服务器
            if (StringUtils.isBlank(key)) {
                key = FtpUtils.DEFAULT_KEY;
            }
            session = this.ftpPool.borrowObject(key);
            T result = callBack.apply(session);
            this.ftpPool.returnObject(key, session);
            return result;
        } catch (Exception e) {
            if (session != null) {
                try {
                    this.ftpPool.invalidateObject(key, session);
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
            throw e;
        }
    }

    /**
     * 单Ftp服务器
     */
    public String getAccessUrl() throws Exception {
        return this.execute(null, (session) -> FtpUtils.buildUrl(session.getConfig()));
    }

    public FtpConfig getConfig() throws Exception {
        return this.execute(null, FtpSession::getConfig);
    }

    public String pwd() throws Exception {
        return this.execute(null, FtpSession::pwd);
    }

    public File downloadFile(String remoteFilePath, String localFilePath) throws Exception {
        return this.execute(null, (session) -> session.downloadFile(remoteFilePath, localFilePath));
    }

    public boolean downloadFile(String remoteFilePath, File localFile) throws Exception {
        return this.execute(null, (session) -> session.downloadFile(remoteFilePath, localFile));
    }

    public boolean downloadFile(String remoteFilePath, OutputStream output) throws Exception {
        return this.execute(null, (session) -> session.downloadFile(remoteFilePath, output));
    }

    public boolean uploadFile(String localFilePath, String remoteFilePath) throws Exception {
        return this.execute(null, (session) -> session.uploadFile(localFilePath, remoteFilePath));
    }

    public boolean uploadFile(File localFile, String remoteFilePath) throws Exception {
        return this.execute(null, (session) -> session.uploadFile(localFile, remoteFilePath));
    }

    public List<String> listNames(String dir) throws Exception {
        return this.execute(null, (session) -> session.listNames(dir));
    }

    public List<String> listNames(String dir, Function<String, Boolean> filter) throws Exception {
        return this.execute(null, (session) -> session.listNames(dir, filter));
    }

    public List<RemoteFile> listFiles(String dirPath) throws Exception {
        return this.execute(null, (session) -> session.listFiles(dirPath));
    }

    public List<RemoteFile> listFiles(String dirPath, Function<RemoteFile, Boolean> filter) throws Exception {
        return this.execute(null, (session) -> session.listFiles(dirPath, filter));
    }

    public List<RemoteFile> listFiles(String dirPath, Date dataStartTime, Date dataEndTime) throws Exception {
        return this.execute(null, (session) -> session.listFiles(dirPath, dataStartTime, dataEndTime));
    }

    public boolean deleteFile(String remoteFilePath) throws Exception {
        return this.execute(null, (session) -> session.deleteFile(remoteFilePath));
    }

    public boolean rename(String oldFilePath, String newFilePath) throws Exception {
        return this.execute(null, (session) -> session.rename(oldFilePath, newFilePath));
    }

    public boolean createDirectory(String dir) throws Exception {
        return this.execute(null, (session) -> session.createDirectory(dir));
    }

    public boolean createDirectoryForce(String dirs) throws Exception {
        return this.execute(null, (session) -> session.createDirectoryForce(dirs));
    }

    public boolean removeDirectory(String path) throws Exception {
        return this.execute(null, (session) -> session.removeDirectory(path));
    }

    public boolean removeDirectoryForce(String path) throws Exception {
        return this.execute(null, (session) -> session.removeDirectoryForce(path));
    }

    public boolean exists(String path, int fileType) throws Exception {
        return this.execute(null, (session) -> session.exists(path, fileType));
    }

    /**
     * 多Ftp服务器
     */
    public String getAccessUrl(String key) throws Exception {
        return this.execute(key, (session) -> FtpUtils.buildUrl(session.getConfig()));
    }

    public FtpConfig getConfig(String key) throws Exception {
        return this.execute(key, FtpSession::getConfig);
    }

    public String pwd(String key) throws Exception {
        return this.execute(key, FtpSession::pwd);
    }

    public File downloadFile(String key, String remoteFilePath, String localFilePath) throws Exception {
        return this.execute(key, (session) -> session.downloadFile(remoteFilePath, localFilePath));
    }

    public boolean downloadFile(String key, String remoteFilePath, File localFile) throws Exception {
        return this.execute(key, (session) -> session.downloadFile(remoteFilePath, localFile));
    }

    public boolean downloadFile(String key, String remoteFilePath, OutputStream output) throws Exception {
        return this.execute(key, (session) -> session.downloadFile(remoteFilePath, output));
    }

    public boolean uploadFile(String key, String localFilePath, String remoteFilePath) throws Exception {
        return this.execute(key, (session) -> session.uploadFile(localFilePath, remoteFilePath));
    }

    public boolean uploadFile(String key, File localFile, String remoteFilePath) throws Exception {
        return this.execute(key, (session) -> session.uploadFile(localFile, remoteFilePath));
    }

    public List<String> listNames(String key, String dir) throws Exception {
        return this.execute(key, (session) -> session.listNames(dir));
    }

    public List<String> listNames(String key, String dir, Function<String, Boolean> filter) throws Exception {
        return this.execute(key, (session) -> session.listNames(dir, filter));
    }

    public List<RemoteFile> listFiles(String key, String dirPath) throws Exception {
        return this.execute(key, (session) -> session.listFiles(dirPath));
    }

    public List<RemoteFile> listFiles(String key, String dirPath, Function<RemoteFile, Boolean> filter) throws Exception {
        return this.execute(key, (session) -> session.listFiles(dirPath, filter));
    }

    public List<RemoteFile> listFiles(String key, String dirPath, Date dataStartTime, Date dataEndTime) throws Exception {
        return this.execute(key, (session) -> session.listFiles(dirPath, dataStartTime, dataEndTime));
    }

    public boolean deleteFile(String key, String remoteFilePath) throws Exception {
        return this.execute(key, (session) -> session.deleteFile(remoteFilePath));
    }

    public boolean rename(String key, String oldFilePath, String newFilePath) throws Exception {
        return this.execute(key, (session) -> session.rename(oldFilePath, newFilePath));
    }

    public boolean createDirectory(String key, String dir) throws Exception {
        return this.execute(key, (session) -> session.createDirectory(dir));
    }

    public boolean createDirectoryForce(String key, String dirs) throws Exception {
        return this.execute(key, (session) -> session.createDirectoryForce(dirs));
    }

    public boolean removeDirectory(String key, String path) throws Exception {
        return this.execute(key, (session) -> session.removeDirectory(path));
    }

    public boolean removeDirectoryForce(String key, String path) throws Exception {
        return this.execute(key, (session) -> session.removeDirectoryForce(path));
    }

    public boolean exists(String key, String path, int fileType) throws Exception {
        return this.execute(key, (session) -> session.exists(path, fileType));
    }

}
