package com.springvue.app.common.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * IMAP邮件信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImapMail extends ImapMailProperties {

    /**
     * 邮件唯一编码
     */
    private String messageId;

    /**
     * 发件人
     */
    private String sender;

    /**
     * 收件人
     */
    private String receiver;

    /**
     * 主题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 附件
     */
    private List<String> attachments;

    /**
     * 创建时间
     */
    private Date createTime;
}
