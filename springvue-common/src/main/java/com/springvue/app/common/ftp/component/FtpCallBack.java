package com.springvue.app.common.ftp.component;


import com.springvue.app.common.ftp.session.FtpSession;

/**
 * Ftp回调接口
 */
@FunctionalInterface
public interface FtpCallBack<T> {

    T apply(FtpSession session) throws Exception;
}
