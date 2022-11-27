package com.springvue.app.dao.model.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName SYS_EMAIL
 */
@TableName(value ="SYS_EMAIL")
@Data
public class SysEmailPo implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 邮件ID
     */
    private String messageId;

    /**
     * 发件人
     */
    private String sender;

    /**
     * 收件人, 用逗号隔开
     */
    private String receivers;

    /**
     * 邮件类型1为发送2为接收
     */
    private Integer msgType;

    /**
     * 主题
     */
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 附件编码, 用逗号隔开
     */
    private String attachments;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}