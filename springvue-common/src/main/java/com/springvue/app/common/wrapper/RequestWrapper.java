package com.springvue.app.common.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 请求封装类
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private String body;

    public String getBody() {
        return body;
    }

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try {
            StringBuilder body = new StringBuilder();
            BufferedReader reader = request.getReader();
            reader.lines().forEach(line -> {
                body.append(line);
            });
            this.body = body.toString();
        } catch (Exception ignored) {
            this.body = null;
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
