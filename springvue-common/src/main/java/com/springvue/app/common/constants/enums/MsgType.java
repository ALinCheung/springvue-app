package com.springvue.app.common.constants.enums;

/**
 * 消息类型
 */
public enum MsgType {

    EMAIL_SEND(1, "发送邮件"),

    EMAIL_RECEIVE(2, "接收邮件")
    ;

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    MsgType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
