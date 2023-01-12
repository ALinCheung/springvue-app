package com.springvue.app.dao.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SysEmailVo implements Serializable {
    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 收件人列表
     */
    private List<String> receivers;

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
     * 附件路径列表
     */
    private List<String> attachments;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}