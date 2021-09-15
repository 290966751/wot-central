package com.wot.exception;

/**
 * 基础异常枚举
 */
public enum EnumErrorDesc implements IErrorDesc{

    /**参数错误**/
    PARAM_ERROR("ERROR","1001","参数错误"),

    ;

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

    EnumErrorDesc(String errorLevel, String errorCode, String errorInfo) {
        this.errorLevel = errorLevel;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    @Override
    public String getErrorLevel() {
        return errorLevel;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorInfo() {
        return errorInfo;
    }

}
