package com.wot.base;

import java.io.Serializable;

/**
 * 异常返回结果
 */
public class ResultError implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public ResultError() {
        this("", "", "");
    }

    public ResultError(String errorLevel, String errorCode, String errorInfo) {
        this.errorLevel = errorLevel;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
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
