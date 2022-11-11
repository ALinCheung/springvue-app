package com.springvue.app.common.ftp.session;

import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.model.RemoteFile;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Ftp会话接口
 */
public interface FtpSession {

    /**
     * 返回配置信息
     */
    FtpConfig getConfig();

    /**
     * 连接和登录
     */
    void connect() throws Exception;

    /**
     * 静默关闭连接
     */
    void close();

    /**
     * 当前目录
     */
    String pwd() throws Exception;

    /**
     * 切回主目录(若没配置主目录,则连接后进入的目录为主目录)
     */
    Boolean home() throws Exception;

    /**
     * 返回连接是否可用
     */
    Boolean isAvailable();

    /**
     * 进入一级或多级目录
     */
    Boolean cd(String dir) throws Exception;

    /**
     * 下载文件,自动创建本地目录
     */
    File downloadFile(String remoteFilePath, String localFilePath) throws Exception;

    /**
     * 下载文件,自动创建本地目录
     */
    Boolean downloadFile(String remoteFilePath, File localFile) throws Exception;

    /**
     * 下载文件,自动创建本地目录
     */
    Boolean downloadFile(String remoteFilePath, OutputStream output) throws Exception;

    /**
     * 上传文件,自动创建远程目录
     */
    Boolean uploadFile(String localFilePath, String remoteFilePath) throws Exception;

    /**
     * 上传文件,自动创建远程目录
     */
    Boolean uploadFile(File localFile, String remoteFilePath) throws Exception;

    /**
     * 列出该目录下的所有文件或目录的名称
     */
    List<String> listNames(String dirPath) throws Exception;

    /**
     * 列出该目录下的所有文件或目录的名称
     */
    List<String> listNames(String dirPath, Function<String, Boolean> filter) throws Exception;

    /**
     * 列出该目录下的所有文件或目录
     */
    List<RemoteFile> listFiles(String dirPath) throws Exception;

    /**
     * 列出该目录下的所有文件或目录
     */
    List<RemoteFile> listFiles(String dirPath, Date dataStartTime, Date dataEndTime) throws Exception;

    /**
     * 列出该目录下的所有文件或目录
     */
    List<RemoteFile> listFiles(String dirPath, Function<RemoteFile, Boolean> fileFilter) throws Exception;

    /**
     * 删除文件
     */
    Boolean deleteFile(String remoteFilePath) throws Exception;

    /**
     * 重命名文件
     */
    Boolean rename(String oldFilePath, String newFilePath) throws Exception;

    /**
     * 创建一个目录 若想递归创建目录参考createDirectoryForce
     */
    Boolean createDirectory(String dir) throws Exception;

    /**
     * 递归创建目录
     */
    Boolean createDirectoryForce(String dirs) throws Exception;

    /**
     * 删除一个空目录,若想强制删除目录参考removeDirectoryForce(path);
     */
    Boolean removeDirectory(String path) throws Exception;

    /**
     * 强制删除目录
     */
    Boolean removeDirectoryForce(String path) throws Exception;

    /**
     * 检查文件、目录是否存在。RemoteFile.FILE_TYPE 为文件 ， RemoteFile.DIRECTORY_TYPE为目录
     */
    Boolean exists(String path, int fileType) throws Exception;

}
