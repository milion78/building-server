package com.tencent.wxcloudrun.config;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 响应包装器
 * 用于缓存响应内容，以便在拦截器中读取
 * 
 * @author building-service
 */
public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {
    
    private ByteArrayOutputStream content = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    
    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    content.write(b);
                }
                
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setWriteListener(WriteListener listener) {
                    // 不需要实现
                }
            };
        }
        return outputStream;
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(content, getCharacterEncoding()));
        }
        return writer;
    }
    
    /**
     * 获取响应内容
     */
    public byte[] getContentAsBytes() {
        if (writer != null) {
            writer.close();
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content.toByteArray();
    }
    
    /**
     * 获取响应内容字符串
     */
    public String getContentAsString() {
        try {
            return new String(getContentAsBytes(), getCharacterEncoding());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

