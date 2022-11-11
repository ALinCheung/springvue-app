package com.springvue.app.common.ftp.session;

import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.config.FtpConfigImpl;
import com.springvue.app.common.ftp.model.FileType;
import com.springvue.app.common.ftp.model.RemoteFile;
import com.springvue.app.common.ftp.model.SystemType;
import com.springvue.app.common.ftp.util.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Ftp会话实现类
 */
@Slf4j
public class FtpSessionImpl implements FtpSession {

    private final FtpConfigImpl config;

    private FTPClient ftpClient = null;

    private String getFailReason() {
        return this.ftpClient.getReplyString();
    }

    public FtpSessionImpl(FtpConfigImpl config) {
        this.config = config;
    }

    @Override
    public FtpConfig getConfig() {
        return this.config;
    }

    @Override
    public void connect() throws Exception {
        this.close();
        this.ftpClient = new FTPClient();
        // 设置客户端配置
        FTPClientConfig ftpClientConfig = this.getFTPClientConfig();
        this.ftpClient.configure(ftpClientConfig);
        this.ftpClient.setRemoteVerificationEnabled(this.config.isRemoteVerificationEnabled());
        if (this.config.getDataTimeout() > 0) {
            this.ftpClient.setDataTimeout(this.config.getDataTimeout());
        }
        this.ftpClient.setAutodetectUTF8(true);
        this.ftpClient.setControlEncoding(this.config.getControlEncoding());
        this.ftpClient.setConnectTimeout(this.config.getConnectTimeOutMs());
        this.ftpClient.setRemoteVerificationEnabled(false);
        // 连接ftp
        this.ftpClient.connect(this.config.getHost(), this.config.getPort());
        // 获取响应码
        int reply = this.ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            this.close();
            throw new IOException(this.getFailReason());
        }
        // 登录
        Boolean loginStatus = this.ftpClient.login(this.config.getUsername(), this.config.getPassword());
        if (!loginStatus) {
            throw new IOException(this.getFailReason());
        }
        // 设置客户端模式
        if (this.config.isPassiveMode()) {
            this.ftpClient.enterLocalPassiveMode();
        }
        // 设置文件类型
        if (this.config.getFileType() == FileType.BINARY_FILE_TYPE) {
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            this.ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
        }
        // 切换工作目录
        if (this.config.getHome() == null || this.config.getHome().trim().length() == 0) {
            String home = this.pwd();
            log.debug("session[{}] set home[{}]", this.config.getHost(), home);
            this.config.setHome(home);
        } else if (!this.home()) {
            throw new IOException("切换主目录失败[" + this.config.getHome() + "]");
        }
    }

    /**
     * 获取ftp客户端配置
     */
    private FTPClientConfig getFTPClientConfig() {
        FTPClientConfig ftpClientConfig;
        if (this.config.getSystemKey() == SystemType.UNIX) {
            ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        } else if (this.config.getSystemKey() == SystemType.WINDOWS) {
            ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
        } else {
            ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        }
        if (this.config.getServerLanguageCode() != null && this.config.getServerLanguageCode().trim().length() > 0) {
            ftpClientConfig.setServerLanguageCode(this.config.getServerLanguageCode());
        }
        if (this.config.getDefaultDateFormatStr() != null && this.config.getDefaultDateFormatStr().trim().length() > 0) {
            ftpClientConfig.setDefaultDateFormatStr(this.config.getDefaultDateFormatStr());
        }
        if (this.config.getRecentDateFormatStr() != null && this.config.getRecentDateFormatStr().trim().length() > 0) {
            ftpClientConfig.setRecentDateFormatStr(this.config.getRecentDateFormatStr());
        }
        if (this.config.getServerTimeZoneId() != null && this.config.getServerTimeZoneId().trim().length() > 0) {
            ftpClientConfig.setServerTimeZoneId(this.config.getServerTimeZoneId());
        }
        return ftpClientConfig;
    }

    @Override
    public void close() {
        if (this.ftpClient != null) {
            try {
                this.ftpClient.disconnect();
            } catch (Exception ignored) {
            }
        }
        this.ftpClient = null;
    }

    @Override
    public String pwd() throws Exception {
        return this.ftpClient.printWorkingDirectory();
    }

    @Override
    public Boolean home() throws Exception {
        if (this.config.getHome() != null && this.config.getHome().trim().length() > 0) {
            return this.cd(this.config.getHome());
        }
        return true;
    }

    @Override
    public Boolean isAvailable() {
        Boolean available;
        try {
            available = this.ftpClient.sendNoOp();
        } catch (Exception e) {
            available = false;
        }
        return available;
    }

    @Override
    public Boolean cd(String dir) throws Exception {
        return this.ftpClient.changeWorkingDirectory(dir);
    }

    @Override
    public File downloadFile(String remoteFilePath, String localFilePath) throws Exception {
        File localFile = new File(localFilePath);
        if (this.downloadFile(remoteFilePath, localFile)) {
            return localFile;
        } else {
            throw new IOException("download file failed: " + remoteFilePath);
        }
    }

    @Override
    public Boolean downloadFile(String remoteFilePath, File localFile) throws Exception {
        if (localFile == null) {
            return false;
        }
        File realLocalFile = localFile.getAbsoluteFile();
        FileOutputStream fos = null;
        Boolean success = false;
        try {
            String destDir = realLocalFile.getParent();
            File dir = new File(destDir);
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
                if (!mkdirs) {
                    throw new IOException("创建本地目录[" + destDir + "]失败");
                }
            }
            fos = new FileOutputStream(realLocalFile);
            success = this.downloadFile(remoteFilePath, fos);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ignored) {
            }
        }
        return success;
    }

    @Override
    public Boolean downloadFile(String remoteFilePath, OutputStream output) throws Exception {
        return this.ftpClient.retrieveFile(remoteFilePath, output);
    }

    @Override
    public Boolean uploadFile(String localFilePath, String remoteFilePath) throws Exception {
        return this.uploadFile(new File(localFilePath), remoteFilePath);
    }

    @Override
    public Boolean uploadFile(File localFile, String remoteFilePath) throws Exception {
        String dir = null;
        String filePath;

        if (!remoteFilePath.contains("/")) {
            filePath = remoteFilePath;
        } else if (remoteFilePath.equals("/")) {
            dir = remoteFilePath;
            filePath = dir + localFile.getName();
        } else if (remoteFilePath.endsWith("/")) {
            dir = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
            filePath = dir + "/" + localFile.getName();
        } else {
            dir = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
            filePath = remoteFilePath;
        }

        FileInputStream fis = null;
        try {
            this.createDirectoryForce(dir);
            fis = new FileInputStream(localFile);
            if (this.ftpClient.storeFile(filePath, fis)) {
                return true;
            } else {
                throw new IOException(this.getFailReason());
            }

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public List<String> listNames(String dirPath) throws Exception {
        return this.listNames(dirPath, null);
    }

    @Override
    public List<String> listNames(String dirPath, Function<String, Boolean> filter) throws Exception {
        String[] fileNames = this.ftpClient.listNames(dirPath);
        List<String> list = new ArrayList<>();
        for (String name : fileNames) {
            if (filter == null || filter.apply(name)) {
                list.add(name);
            }
        }
        return list;
    }

    @Override
    public List<RemoteFile> listFiles(String dirPath) throws Exception {
        return this.listFiles(dirPath, null);
    }

    @Override
    public List<RemoteFile> listFiles(String dirPath, Function<RemoteFile, Boolean> fileFilter) throws Exception {
        List<RemoteFile> files = new ArrayList<>();
        if (fileFilter == null) {
            this.ftpClient.listFiles(dirPath, ftpFile -> {
                RemoteFile remoteFile = this.formatRemoteFile(ftpFile, dirPath);
                files.add(remoteFile);
                return true;
            });
        } else {
            this.ftpClient.listFiles(dirPath, ftpFile -> {
                RemoteFile remoteFile = this.formatRemoteFile(ftpFile, dirPath);
                if (fileFilter.apply(remoteFile)) {
                    files.add(remoteFile);
                    return true;
                } else {
                    return false;
                }
            });
        }
        return files;
    }

    @Override
    public List<RemoteFile> listFiles(String dirPath, Date dataStartTime, Date dataEndTime) throws Exception {
        return this.listFiles(dirPath, fileFtp -> {
            if (fileFtp == null)
                return false;
            Date modifyTime = fileFtp.getModifyTime();
            return FtpUtils.compare(modifyTime, dataStartTime, dataEndTime);
        });
    }

    private RemoteFile formatRemoteFile(FTPFile ftpFile, String dir) {
        if (ftpFile == null) {
            return null;
        }
        RemoteFile file = new RemoteFile();
        file.setDir(dir);
        file.setFileName(ftpFile.getName());
        file.setModifyTime(ftpFile.getTimestamp().getTime());
        file.setSize(ftpFile.getSize());
        file.setType(ftpFile.getType());
        return file;
    }

    @Override
    public Boolean deleteFile(String remoteFilePath) throws Exception {
        return this.ftpClient.deleteFile(remoteFilePath);
    }

    @Override
    public Boolean rename(String oldFilePath, String newFilePath) throws Exception {
        return this.ftpClient.rename(oldFilePath, newFilePath);
    }

    @Override
    public Boolean createDirectoryForce(String dirs) throws Exception {
        String currentDir = this.pwd();
        Boolean isCreated = false;
        try {
            if (dirs == null || dirs.trim().length() == 0 ||
                    dirs.trim().equals(".") || dirs.trim().equalsIgnoreCase("/")) {
                return isCreated;
            }

            if (dirs.startsWith("/")) {
                if (!this.cd("/")) {
                    throw new IOException("切换/目录失败");
                }
            }

            String[] dirList;
            if (!dirs.contains("/")) {
                dirList = new String[1];
                dirList[0] = dirs;
            } else {
                dirList = dirs.split("/");
            }

            for (String dir : dirList) {
                if (dir == null || dir.trim().length() == 0 || dir.trim().equals(".")) {
                    continue;
                } else if (dir.trim().equals("..")) {
                    this.cd(dir);
                }
                if (!this.cd(dir)) {
                    if (this.createDirectory(dir)) {
                        isCreated = true;
                        this.cd(dir);
                    } else {
                        isCreated = false;
                    }
                }
            }
        } finally {
            this.cd(currentDir);
        }
        return isCreated;
    }

    @Override
    public Boolean createDirectory(String dir) throws Exception {
        if (!this.ftpClient.makeDirectory(dir)) {
            return FTPReply.isPositiveCompletion(this.ftpClient.sendCommand("mkdir " + dir));
        } else {
            return true;
        }
    }

    @Override
    public Boolean removeDirectory(String path) throws Exception {
        return this.ftpClient.removeDirectory(path);
    }

    @Override
    public Boolean removeDirectoryForce(String path) throws Exception {
        if (path == null || path.trim().length() == 0 || path.trim().equals("/")) {
            return false;
        }
        List<RemoteFile> remoteFiles = this.listFiles(path);
        if (remoteFiles == null || remoteFiles.size() == 0) {
            return this.removeDirectory(path);
        }

        for (RemoteFile ftpFile : remoteFiles) {
            String name = ftpFile.getFileName();
            if (ftpFile.getType() == RemoteFile.DIRECTORY_TYPE) {
                this.removeDirectoryForce(path + "/" + name);
            } else {
                this.deleteFile(path + "/" + name);
            }
        }
        return this.removeDirectory(path);
    }

    @Override
    public Boolean exists(String path, int fileType) throws Exception {
        if (fileType == RemoteFile.DIRECTORY_TYPE) {
            String pwd = this.pwd();
            Boolean ret = this.cd(path);
            this.cd(pwd);
            return ret;
        } else {
            FTPFile[] ftpFiles = this.ftpClient.listFiles(path);
            if (ftpFiles == null || ftpFiles.length != 1) {
                return false;
            }
            return ftpFiles[0].getType() == FTPFile.FILE_TYPE;
        }
    }
}
