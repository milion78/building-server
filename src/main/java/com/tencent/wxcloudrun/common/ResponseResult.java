package com.tencent.wxcloudrun.common;

/**
 * 统一响应结果封装类
 * 
 * @author building-service
 */
public class ResponseResult<T> {
    
    /**
     * 状态码，0表示成功
     */
    private Long status;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;
    
    public ResponseResult() {
    }
    
    public ResponseResult(Long status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(0L, "success", data);
    }
    
    /**
     * 失败响应
     */
    public static <T> ResponseResult<T> error(Long status, String msg) {
        return new ResponseResult<>(status, msg, null);
    }
    
    public Long getStatus() {
        return status;
    }
    
    public void setStatus(Long status) {
        this.status = status;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}

