package com.wot.oracle.bop.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class BopFunConstantSqlInit extends BopConfigSqlInit {

    private final static String staticFinalStr = "public static final String";
    private final static String tableInit = "DROP TABLE IF EXISTS `db_pub`.`bop_config_func_constant_yql`;\n" +
            "CREATE TABLE `db_pub`.bop_config_func_constant_yql(\n" +
            "\t`row_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "\t`func_num` int(11) NOT NULL,\n" +
            "\t`fun_constant` varchar(100) NOT NULL,\n" +
            "\tPRIMARY KEY (`row_id`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;";
    /** 新增原始sql */
    private static final String addSql = "INSERT INTO `db_pub`.`bop_config_func_constant_yql` (`func_num`, `fun_constant`) VALUES (";

    private void initSql(String filePath, String targetPath) {
        //1、读取
        List<String> funConstantList = this.readBopConfigTxt(filePath);
        Iterator<String> iterator = funConstantList.iterator();

        List<String> sqlList = new ArrayList<>(funConstantList.size());
        sqlList.add(tableInit);
        sqlList.add("\r\n\r\n");

        StringBuilder sbd = new StringBuilder();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next == null || next.length() <= 0) {
                iterator.remove();
                continue;
            }
            next = next.trim();
            if (!next.startsWith(staticFinalStr)) {
                System.out.println("丢弃:" + next);
                iterator.remove();
                continue;
            }
            String constant = next.substring(next.indexOf(staticFinalStr) + staticFinalStr.length() + 1, next.indexOf("=") - 1);
            String func = next.substring(next.indexOf("\"") + 1, next.lastIndexOf("\""));
            sbd.append(addSql)
                    .append(func).append(", ")
                    .append("'").append(constant).append("'")
                    .append(");");
            sqlList.add(sbd.toString());
            sbd.setLength(0);
        }

        //4、写入文本
        this.writerText(targetPath, sqlList);
    }

    public static void main(String[] args) {
//        String next = "public static final String CNST_FUNCID_SYSARG_ALLBRACNH_ADD = \"110100\";\t// 券商机构增加";
//        String constant = next.substring(next.indexOf(staticFinalStr) + staticFinalStr.length() + 1, next.indexOf("=") - 1);
//        String func = next.substring(next.indexOf("\"") + 1, next.lastIndexOf("\""));
//        System.out.println(constant);
//        System.out.println(func);
        new BopFunConstantSqlInit().initSql("C:\\Users\\luoding\\Desktop\\tmp\\UFFunction.java", "D:\\LDtrader\\方正\\BOP\\菜单功能号整理\\7-bopFunConstantSqlInit.sql");
    }

}
