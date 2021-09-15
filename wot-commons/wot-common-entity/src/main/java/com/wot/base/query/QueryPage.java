/**
 * 系统名称:
 * 模块名称:
 * 创建者：     zhouwm
 * 创建时间： 2018年5月24日上午8:29:33
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
package com.wot.base.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    protected int totalCount;

    /**
     * 每页记录数大小
     */
    protected int pageSize = 50;

    /**
     * 查询第几页
     */
    protected int pageNo = 1;

    /**
     * 排序字段,如: row_id desc, create_date asc
     */
    private String order;

    /**
     * 分页查询返回的结果List
     */
    List<T> data;

    /**
     * 获取开始的记录条数序号,从0开始
     *
     * @return
     */
    public int getStart() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotalPages() {
        if (totalCount % pageSize == 0) {
            return totalCount / pageSize;
        } else {
            return totalCount / pageSize + 1;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<T> getData() {
        if (data == null) {
            data = new ArrayList<T>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
