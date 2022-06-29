package com.wot.generator.txt;

import com.wot.generator.api.IntrospectedColumn;
import com.wot.generator.config.Context;
import com.wot.generator.config.TableConfiguration;
import com.wot.generator.constant.GeneratorConstant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * txt获取表
 */
public class TxtContext extends Context {

    /**
     * 获取表字段
     */
    @Override
    public void introspectTables() {
        String txtPath = this.getProperty(GeneratorConstant.TXT_PATH);
        File file = new File(txtPath);
        if (!file.exists()) {
            throw new RuntimeException("txt获取表>txt文件不存在");
        }
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader bf = null;
        try {
            is = new FileInputStream(file);
            isr = new InputStreamReader(is);
            bf = new BufferedReader(isr);
            String line;
            int i = 0;
            TableConfiguration tab = new TableConfiguration();
            while ((line = bf.readLine()) != null) {
                line = line.trim();
                System.out.println(i + ">" + line);
//                line.trim()
                if (Pattern.matches("^[A-Za-z].*", line)) {
                    String[] split = line.split("\t");
                    if (split.length <= 0) {
                        continue;
                    }
                    String actualColumnName = split[0];
                    if (actualColumnName.length() <= 0 || !Pattern.matches("^[A-Za-z_]*$", actualColumnName)) {
                        continue;
                    }
                    IntrospectedColumn column = new IntrospectedColumn();
                    column.setActualColumnName(actualColumnName);
                    column.setJdbcTypeName("String");
                    column.setRemarks(this.getColumnRemarks(split, line));
                    tab.addColumn(column);
                }
            }
            this.addTableConfiguration(tab);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void calculateJavaModelGenerators() {

    }

    /**
     * 获取字段备注
     * @param fields 字段
     * @param defaultValue 默认值
     * @return 获取字段备注
     */
    private String getColumnRemarks(String[] fields, String defaultValue) {
        if (fields.length != 6 && fields.length != 7) {
            return defaultValue;
        }
        /**
         * 中登辅助证件类别、必填；境内机构必填，境外机构非必填，境内机构仅能填写“组织机构代码证”;个人客户使用“港澳台居民居住证”作为主要身份证明文件时，辅助身份证明文件类别、辅助身份证明文件代码必填，辅助身份证明文件类别仅能填写“港澳居民来往内地通行证”或“台湾居民来往大陆通行证”;个人客户使用“港澳居民来往内地通行证”或“台湾居民来往大陆通行证”作为主要身份证明文件时，辅助身份证明文件类别、辅助身份证明文件代码为选填项（非必填），如需填写辅助身份证明文件类别，仅能填写“港澳台居民居住证”。其他个人不填。
         *
         */
        StringBuilder sbd = new StringBuilder();
        sbd.append(fields[2]).append("[");
        if ("是".equals(fields[3].trim())) {
            sbd.append("必填");
        } else {
            sbd.append("非必填");
        }
        sbd.append("]");
        if (fields.length == 7) {
            sbd.append(",");
            String docRemark = fields[6];
            if (docRemark.length() > 1 && docRemark.startsWith("\"")) {
                docRemark = docRemark.substring(1);
            }
            sbd.append(docRemark);
        }
        return sbd.toString();
    }

    @Override
    public void generateFiles() {

    }

    public static void main(String[] args) {
        TxtContext context = new TxtContext();
        context.addProperty(GeneratorConstant.TXT_PATH, "C:\\Users\\luoding\\Desktop\\tmp\\a.txt");
        context.introspectTables();
        TableConfiguration tableConfiguration = context.getTableConfigurations().get(0);
        for (IntrospectedColumn column : tableConfiguration.getColumns()) {
            System.out.println(column.getActualColumnName());
        }
    }

}
