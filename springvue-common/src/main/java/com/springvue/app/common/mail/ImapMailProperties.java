package com.springvue.app.common.mail;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * IMAP配置条件
 */
@Data
public class ImapMailProperties {

    /**
     * 邮件接收地址
     */
    @NotBlank(message = "邮件接收地址不能为空")
    private String host;

    /**
     * 邮件接收端口
     */
    @NotNull(message = "邮件接收端口不能为空")
    private Integer port;

    /**
     * 邮件接收是否开启ssl
     */
    @NotNull(message = "邮件接收是否开启ssl不能为空")
    private Boolean isSsl = false;

    /**
     * 邮件接收用户名
     */
    @NotBlank(message = "邮件接收用户名不能为空")
    private String username;

    /**
     * 邮件接收密码/授权码
     */
    @NotBlank(message = "邮件接收密码/授权码不能为空")
    private String password;

    /**
     * 邮件接收附件存储目录
     */
    private String attachmentDir;

    /**
     * 收件箱名称
     */
    @NotBlank(message = "收件箱名称不能为空")
    private String inboxName = "INBOX";

    /**
     * 垃圾邮件文件夹名称
     */
    private String trashFolderName = "垃圾邮件";

    /**
     * 历史邮件文件夹名称
     */
    private String historyFolderName = "历史邮件";

    /**
     * 收件箱保存的邮件数(防止每次读取过量邮件)
     */
    private Integer inBoxCount = 10;

    /**
     * 批量接收的邮件数
     */
    private Integer batchCount = 5;
}
