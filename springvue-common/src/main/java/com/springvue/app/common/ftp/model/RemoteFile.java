package com.springvue.app.common.ftp.model;

import lombok.Data;

import java.util.Date;

/**
 * 远程文件实体
 */
@Data
public class RemoteFile {

    /**
     * 远程文件类型0: 文件
     */
    public static final int FILE_TYPE = 0;

    /**
     * 远程文件类型1: 目录
     */
    public static final int DIRECTORY_TYPE = 1;

    /**
     * 远程文件类型3: 未知文件
     */
    public static final int UNKNOWN_TYPE = 3;

    private String dir;

    private String fileName;

    private Date modifyTime;

    private long size;

    private int type;

    public RemoteFile(String dir, String fileName, Date modifyTime, long size, int type) {
        this.dir = dir;
        this.fileName = fileName;
        this.modifyTime = modifyTime;
        this.size = size;
        this.type = type;
    }

    public RemoteFile() {
        this.type = RemoteFile.UNKNOWN_TYPE;
        this.size = -1; // 0 is valid, so use -1
    }

    public String getFullPath() {
        if (this.dir.endsWith("/") || this.dir.endsWith("\\")) {
            return this.dir + this.fileName;
        } else {
            return this.dir + "/" + this.fileName;
        }
    }

    @Override
    public String toString() {
        return "fileName[" + this.fileName +
                "]type[" + this.type +
                "]size[" + this.size +
                "]modify[" + this.modifyTime + "]";
    }

}
