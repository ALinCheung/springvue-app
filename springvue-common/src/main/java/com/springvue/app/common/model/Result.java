package com.springvue.app.common.model;

import com.springvue.app.common.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 请求响应结果
 * @param <T>
 */
@Schema(description = "请求响应结果")
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码", example = "0")
    private Integer code = CommonConstant.SUCCESS;

    @Schema(description = "响应消息", example = "success")
    private String msg = CommonConstant.SUCCESS_TEXT;

    @Schema(description = "响应数据")
    private T data;

    public static Result success() {
        return new Result().setCode(CommonConstant.SUCCESS).setMsg(CommonConstant.SUCCESS_TEXT);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>().setCode(CommonConstant.SUCCESS).setMsg(CommonConstant.SUCCESS_TEXT).setData(data);
    }

    public static Result process() {
        return new Result().setCode(CommonConstant.PROCESS).setMsg(CommonConstant.PROCESS_TEXT);
    }

    public static <T> Result<T> process(T data) {
        return new Result<T>().setCode(CommonConstant.PROCESS).setMsg(CommonConstant.PROCESS_TEXT).setData(data);
    }

    public static Result fail() {
        return new Result().setCode(CommonConstant.FAIL).setMsg(CommonConstant.FAIL_TEXT);
    }

    public static Result fail(String msg) {
        return new Result().setCode(CommonConstant.FAIL).setMsg(msg);
    }
}
