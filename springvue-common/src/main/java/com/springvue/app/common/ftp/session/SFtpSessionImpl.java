package com.springvue.app.common.ftp.session;

import com.jcraft.jsch.*;
import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.config.SFtpConfigImpl;
import com.springvue.app.common.ftp.model.RemoteFile;
import com.springvue.app.common.ftp.util.FtpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * SFtp会话实现类
 */
@Slf4j
public class SFtpSessionImpl implements FtpSession {

    private final SFtpConfigImpl config;

    private Session sshSession;

    private ChannelSftp sftp;

    public SFtpSessionImpl(SFtpConfigImpl config) {
        this.config = config;
    }

    @Override
    public FtpConfig getConfig() {
        return this.config;
    }

    @Override
    public void connect() throws Exception {
        try {
            JSch jsch = new JSch();
            this.sshSession = jsch.getSession(this.config.getUsername(), this.config.getHost(),this.config.getPort());
            this.sshSession.setTimeout(this.config.getConnectTimeOutMs());
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            this.sshSession.setUserInfo(new SFtpUserInfo(this.config.getPassword()));
            this.sshSession.setConfig(sshConfig);
        } catch (JSchException e) {
            this.processIOException("init: " + this.config.getHost() + ":"
                    + this.config.getPort() + " failed! reason: " + e.getMessage(), e);
        }

        try {
            this.sshSession.connect();
            Channel channel = this.sshSession.openChannel("sftp");
            channel.connect();
            this.sftp = (ChannelSftp) channel;
            if (this.config.getControlEncoding() != null
                    && this.config.getControlEncoding().trim().length() > 0) {
                Field field = this.sftp.getClass().getDeclaredField("fEncoding");
                field.setAccessible(true);
                field.set(this.sftp, this.config.getControlEncoding());
            }
        } catch (Exception e) {
            this.processIOException("connect: " + this.config.getHost() + ":"
                    + this.config.getPort() + " failed! reason: " + e.getMessage(), e);
        }

        if (this.config.getHome() == null || this.config.getHome().trim().length() == 0) {
            String home = this.pwd();
            this.config.setHome(home);
            log.debug("session[{}] set home[{}]", this.config.getHost(), home);
        } else {
            if (!this.home()) {
                throw new IOException("切换主目录失败[" + this.config.getHome() + "]");
            }
        }
    }

    @Override
    public void close() {
        if (this.sftp != null) {
            try {
                this.sftp.disconnect();
            } catch (Exception ignored) {
            }
        }
        if (this.sshSession != null) {
            try {
                this.sshSession.disconnect();
            } catch (Exception ignored) {
            }
        }
        this.sftp = null;
        this.sshSession = null;
    }

    @Override
    public String pwd() throws Exception {
        try {
            return this.sftp.pwd();
        } catch (Exception e) {
            this.processIOException("pwd failed! reason: " + e.getMessage(), e);
        }
        return null;
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
            this.sshSession.sendKeepAliveMsg();
            available = true;
        } catch (Exception e) {
            available = false;
        }
        return available;
    }

    @Override
    public Boolean cd(String dir) throws Exception {
        try {
            this.sftp.cd(dir);
            return true;
        } catch (SftpException e) {
            String message = e.getMessage();
            if (message != null && message.contains("No such file")) {
                log.warn("目录[" + dir + "]不存在");
            } else {
                this.processIOException("cd " + dir + " failed! reason: " + e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public File downloadFile(String remoteFilePath, String localFilePath) throws Exception {
        File localFile = new File(localFilePath);
        if (this.downloadFile(remoteFilePath, localFile)) {
            return localFile;
        } else {
            throw new RuntimeException("download file failed: " + remoteFilePath);
        }
    }

    @Override
    public Boolean downloadFile(String remoteFilePath, File localFile) throws Exception {
        if (localFile == null) {
            return false;
        }
        File realLocalFile = localFile.getAbsoluteFile();
        String destDir = realLocalFile.getParent();
        File dir = new File(destDir);
        if (!dir.exists()) {
            Boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                throw new IOException("创建本地目录[" + destDir + "]失败");
            }
        }
        try {
            this.sftp.get(remoteFilePath, realLocalFile.getAbsolutePath());
            return true;
        } catch (SftpException e) {
            this.processIOException("下载文件[" + remoteFilePath + "] to [" + realLocalFile.getAbsolutePath() + "]失败:" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean downloadFile(String remoteFilePath, OutputStream output) throws Exception {
        try {
            this.sftp.get(remoteFilePath, output);
            return true;
        } catch (SftpException e) {
            this.processIOException("下载文件[" + remoteFilePath + "] 到输出流失败:" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean uploadFile(String localFilePath, String remoteFilePath) throws Exception {
        return this.uploadFile(new File(localFilePath), remoteFilePath);
    }

    @Override
    public Boolean uploadFile(File localFile, String remoteFilePath) throws Exception {
        FileInputStream fis = null;
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

        try {
            this.createDirectoryForce(dir);
            fis = new FileInputStream(localFile);
            try {
                this.sftp.put(fis, filePath);
                return true;
            } catch (Exception e) {
                this.processIOException("上传文件[" + localFile.getAbsolutePath() + "] to ["
                        + remoteFilePath + "]失败.", e);
            }
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    public List<String> listNames(String dirPath) throws Exception {
        return this.listNames(dirPath, null);
    }

    @Override
    public List<String> listNames(String dirPath, Function<String, Boolean> filter) throws Exception {
        List<String> list = new ArrayList<>();
        try {
            Vector<ChannelSftp.LsEntry> files = this.sftp.ls(dirPath);
            for (ChannelSftp.LsEntry entry : files) {
                String fileName = entry.getFilename();
                if (fileName == null || fileName.trim().length() == 0
                        || fileName.trim().equals(".")
                        || fileName.trim().equalsIgnoreCase("..")) {
                    continue;
                }
                if (filter == null || filter.apply(fileName)) {
                    list.add(fileName);
                }
            }
        } catch (Exception e) {
            this.processIOException("列出所有文件或目录名失败[" + dirPath + "]失败:" + e.getMessage(), e);
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
        try {
            if (fileFilter == null) {
                this.sftp.ls(dirPath).forEach(ftpFile -> {
                    ChannelSftp.LsEntry file = (ChannelSftp.LsEntry) ftpFile;
                    if (!file.getFilename().equals(".") && !file.getFilename().equals("..")) {
                        RemoteFile remoteFile = this.formatRemoteFile((ChannelSftp.LsEntry) ftpFile, dirPath);
                        files.add(remoteFile);
                    }
                });
            } else {
                this.sftp.ls(dirPath).forEach(ftpFile -> {
                    ChannelSftp.LsEntry file = (ChannelSftp.LsEntry) ftpFile;
                    if (!file.getFilename().equals(".") && !file.getFilename().equals("..")) {
                        RemoteFile remoteFile = this.formatRemoteFile((ChannelSftp.LsEntry) ftpFile, dirPath);
                        if (fileFilter.apply(remoteFile)) {
                            files.add(remoteFile);
                        }
                    }
                });
            }
        } catch (Exception e) {
            this.processIOException("列出所有文件或目录信息失败[" + dirPath + "]失败:" + e.getMessage(), e);
        }
        return files;
    }

    @Override
    public List<RemoteFile> listFiles(String dirPath, Date dataStartTime, Date dataEndTime) throws Exception {
        return this.listFiles(dirPath, fileFtp -> {
            if (fileFtp == null) {
                return false;
            }
            Date modifyTime = fileFtp.getModifyTime();
            return FtpUtils.compare(modifyTime, dataStartTime, dataEndTime);
        });
    }

    @Override
    public Boolean deleteFile(String remoteFilePath) throws Exception {
        try {
            this.sftp.rm(remoteFilePath);
            return true;
        } catch (SftpException e) {
            this.processIOException("删除文件[" + remoteFilePath + "]失败:" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean rename(String oldFilePath, String newFilePath) throws Exception {
        try {
            this.sftp.rename(oldFilePath, newFilePath);
            return true;
        } catch (SftpException e) {
            this.processIOException("重命名文件[" + oldFilePath + "] -> [" + newFilePath + "]失败" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean createDirectory(String dir) throws Exception {
        try {
            this.sftp.mkdir(dir);
            return true;
        } catch (Exception e) {
            this.processIOException("创建目录[" + dir + "] 失败" + e.getMessage(), e);
        }
        return false;
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

    private Boolean dirExists(String dir) throws Exception {
        try {
            return this.sftp.lstat(dir).isDir();
        } catch (SftpException e) {
            String dirPath = this.pwd() + "/" + dir;
            if ("No Such File".equals(e.getMessage())) {
                log.info("No Such dir " + dirPath);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("判断目录" + dirPath + "是否存在失败！" + e.getMessage(), e);
                }
            }
            return false;
        }
    }

    private Boolean fileExists(String dir) throws Exception {
        try {
            return !this.sftp.lstat(dir).isDir();
        } catch (SftpException e) {
            String dirPath = this.pwd() + "/" + dir;
            if ("No Such File".equals(e.getMessage())) {
                log.info("No Such dir " + dirPath);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("判断文件" + dirPath + "是否存在失败！" + e.getMessage(), e);
                }
            }
            return false;
        }
    }

    @Override
    public Boolean removeDirectory(String path) throws Exception {
        try {
            this.sftp.rmdir(path);
            return true;
        } catch (Exception e) {
            this.processIOException("删除目录[" + path + "] 失败" + e.getMessage(), e);
        }
        return false;
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
            if (ftpFile.getType() == RemoteFile.DIRECTORY_TYPE
                    && (ftpFile.getFileName().equals(".") || ftpFile.getFileName().equals(".."))) {
                continue;
            }
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
            return this.dirExists(path);
        } else {
            return this.fileExists(path);
        }
    }

    public static class SFtpUserInfo implements UserInfo, UIKeyboardInteractive {
        String pwd = null;

        SFtpUserInfo(String pwd) {
            this.pwd = pwd;
        }


        @Override
        public String getPassword() {
            return this.pwd;
        }

        @Override
        public boolean promptYesNo(String str) {
            return true;
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return true;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {
            log.info("message : " + message);
        }

        @Override
        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
            return new String[]{this.pwd};
        }
    }

    private RemoteFile formatRemoteFile(ChannelSftp.LsEntry sftpFile, String dir) {
        if (sftpFile == null) {
            return null;
        }
        RemoteFile file = new RemoteFile();
        file.setDir(dir);
        file.setFileName(sftpFile.getFilename());
        long time = (long) sftpFile.getAttrs().getMTime();
        Date fileDate = new Date(time * 1000);
        file.setModifyTime(fileDate);
        file.setSize(sftpFile.getAttrs().getSize());
        file.setType(sftpFile.getAttrs().isDir() ? RemoteFile.DIRECTORY_TYPE
                : RemoteFile.FILE_TYPE);
        return file;
    }

    private void processIOException(String errorMessage, Throwable e) throws Exception {
        if (e.getCause() instanceof IOException) {
            throw (IOException) e.getCause();
        } else if (e.getMessage().contains("SocketTimeoutException")) {
            throw new IOException(e.getMessage());
        } else {
            log.info(errorMessage, e);
        }
    }
}
