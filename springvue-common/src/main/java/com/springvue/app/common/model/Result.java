package com.springvue.app.common.model;

import com.springvue.app.common.constants.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 请求响应结果
 * @param <T>
 */
@ApiModel(description = "请求响应结果")
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "响应码", example = "0")
    private Integer code = CommonConstant.SUCCESS;

    @ApiModelProperty(value = "响应消息", example = "success")
    private String msg = CommonConstant.SUCCESS_TEXT;

    @ApiModelProperty(value = "响应数据")
    private T data;

    public static Result success() {
        return new Result().setCode(CommonConstant.SUCCESS).setMsg(CommonConstant.SUCCESS_TEXT);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>().setCode(CommonConstant.SUCCESS).setMsg(CommonConstant.SUCCESS_TEXT).setData(data);
    }

    public static Result fail() {
        return new Result().setCode(CommonConstant.FAIL).setMsg(CommonConstant.FAIL_TEXT);
    }

    public static Result fail(String msg) {
        return new Result().setCode(CommonConstant.FAIL).setMsg(msg);
    }
}
