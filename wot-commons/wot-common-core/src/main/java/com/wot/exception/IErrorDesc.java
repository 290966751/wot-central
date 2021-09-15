/**
 * 系统名称:
 * 模块名称:
 * 创建者：     zhouwm
 * 创建时间： 2019年7月18日上午11:22:20
 * 主要功能：
 * 相关文档:
 * 修改记录:
 * 修改日期      修改人员                     修改说明<BR>
 * ========     ======  ============================================
 * <p>
 * ========     ======  ============================================
 * 评审记录：
 * <p>
 * 评审人员：
 * 评审日期：
 * 发现问题：
 */
package com.wot.exception;

/**
 * 日志枚举基础实现
 */
public interface IErrorDesc {

    /**
     * 异常级别
     * @return
     */
    String getErrorLevel();

    /**
     * 异常代码
     * @return
     */
    String getErrorCode();

    /**
     * 异常具体信息
     * @return
     */
    String getErrorInfo();

}
