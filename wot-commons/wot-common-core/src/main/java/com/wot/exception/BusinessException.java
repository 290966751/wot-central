package com.wot.exception;

import java.io.Serializable;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 8144750292957240157L;

    /**
     * 异常级别
     */
    private final String errorLevel;

    /**
     * 异常代码
     */
    private final String errorCode;

    /**
     * 异常具体信息
     */
    private final String errorInfo;

//    public BusinessException() {
//    }

//    public BusinessException(Throwable cause) {
//        super(cause);
//    }

    /**
     * @param errorLevel 异常级别
     * @param errorCode 异常代码
     * @param errorInfo 异常具体信息
     */
    public BusinessException(String errorLevel, String errorCode, String errorInfo) {
        this.errorLevel = errorLevel;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    /**
     * 通过IErrorDesc 构造一个异常
     * @param errorDesc 异常枚举
     */
    public BusinessException(IErrorDesc errorDesc) {
        this(errorDesc.getErrorLevel(), errorDesc.getErrorCode(), errorDesc.getErrorInfo());
    }

    public String getErrorLevel() {
        return errorLevel;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
