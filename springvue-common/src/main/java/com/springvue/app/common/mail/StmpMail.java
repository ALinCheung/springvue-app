package com.springvue.app.common.mail;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * 发送邮件信息
 */
@Data
public class StmpMail {

    /**
     * 发件人
     */
    @NotBlank(message = "发件人不能为空")
    private String sender;

    /**
     * 接收人
     */
    @NotEmpty(message = "接收人不能为空")
    private Set<String> receivers;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 附件
     */
    private List<String> attachments;
}

