package com.wot.generator.config;

import com.wot.generator.codegen.AbstractJavaGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Context.
 */
public abstract class Context extends PropertyHolder{

    /**
     * 表记录
     */
    protected final List<TableConfiguration> tableConfigurations;

    /** The java model generators. */
    protected List<AbstractJavaGenerator> javaModelGenerators;

    public Context() {
        this.tableConfigurations = new ArrayList<>();
        this.javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
        calculateJavaModelGenerators();
    }

    public List<TableConfiguration> getTableConfigurations() {
        return tableConfigurations;
    }

    public void addTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfigurations.add(tableConfiguration);
    }

    /**
     * 获取表字段
     */
    public abstract void introspectTables();

    public abstract void calculateJavaModelGenerators();

    public void generateFiles() {

    }

}
