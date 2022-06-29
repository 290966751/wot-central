package com.wot.generator.config;

import com.wot.generator.api.IntrospectedColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class TableConfiguration.
 */
public class TableConfiguration extends PropertyHolder {

    /** The base columns. */
    protected final List<IntrospectedColumn> columns;

    public TableConfiguration() {
        this.columns = new ArrayList<>(100);
    }

    /**
     * @return columns
     */
    public List<IntrospectedColumn> getColumns() {
        return columns;
    }

    /**
     * Adds the column.
     *
     * @param introspectedColumn the introspected column
     */
    public void addColumn(IntrospectedColumn introspectedColumn) {
        this.columns.add(introspectedColumn);
    }

}
