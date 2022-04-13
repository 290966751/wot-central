package com.wot.oracle.bop.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DROP TABLE IF EXISTS `db_pub`.`bop_config_func_oracle_table_yql`;
 * CREATE TABLE `db_pub`.`bop_config_func_oracle_table_yql` (
 *   `row_id` int(11) NOT NULL AUTO_INCREMENT,
 *   `func_num` int(11) NOT NULL,
 *   `func_name` varchar(100) NOT NULL,
 *   `oracle_func_table` varchar(30) NOT NULL,
 *   PRIMARY KEY (`row_id`) USING BTREE
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
 *
 * //获取菜单对应表
 * SELECT a.oracle_menu_id,a.oracle_menu_caption,GROUP_CONCAT(DISTINCT b.oracle_func_table ORDER BY b.oracle_func_table separator '\r') FROM bop_config_yql a
 *  LEFT JOIN bop_config_func_oracle_table_yql b on a.func_num = b.func_num
 * WHERE b.row_id IS NOT NULL AND b.oracle_func_table != '无'
 * GROUP BY a.oracle_menu_id
 * ORDER BY a.oracle_menu_id
 */
public class BopConfigFuncOracleTacleSqlInit extends BopConfigSqlInit {

    private static final String tableSql = "DROP TABLE IF EXISTS `db_pub`.`bop_config_func_oracle_table_yql`;\n" +
            "CREATE TABLE `db_pub`.`bop_config_func_oracle_table_yql` (\n" +
            "  `row_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `func_num` int(11) NOT NULL,\n" +
            "  `func_name` varchar(100) NOT NULL,\n" +
            "  `oracle_func_table` varchar(30) NOT NULL,\n" +
            "  PRIMARY KEY (`row_id`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;";
    /** 新增原始sql */
    private static final String addSql = "INSERT INTO `db_pub`.`bop_config_func_oracle_table_yql` (`func_num`, `func_name`, `oracle_func_table`) VALUES (";

    private void initSql(String filePath, String targetPath) {
        List<String> list = this.readBopConfigTxt(filePath);
        Map<String, String> sqlGroupMap = new HashMap<>(list.size());
//        Set<String> tableSet = new HashSet<>(list.size());
        List<String> sqlResultList = new ArrayList<>(list.size());
        sqlResultList.add(tableSql);
        sqlResultList.add("\r\n\r\n");

        Iterator<String> iterator = list.iterator();
        StringBuilder sbd = new StringBuilder();
        String funcNum = "", funcName = "";
        boolean start = true;
        while (iterator.hasNext()) {
            String next = iterator.next();
//            //是第一个
//            if (start) {
//                //添加描述信息
//                sbd.append(sqlNotes).append(next);
//                sqlResultList.add(sbd.toString());
//                sbd.setLength(0);
//
//                start = false;
//                iterator.remove();
//                continue;
//            }
//
//            //生成sql
//            if () {
//
//            }

            //分割开
            if (next.startsWith("(")) {

                if (!sqlGroupMap.isEmpty()) {
                    //有值，则把sql写在表下
                    sqlResultList.addAll(this.hashSql(sqlGroupMap));
                    //把上一个菜单的分隔开
//                    sbd.append("\r\n");
                    sqlResultList.add("\r\n\r\n");
                    sqlGroupMap.clear();
                }

                String[] funcNumAndNameArrays = this.splitFuncNumAndName(next);
                funcNum = funcNumAndNameArrays[0];
                funcName = funcNumAndNameArrays[1];

                //添加描述信息
                sbd.append(sqlNotes).append(next);
                sqlResultList.add(sbd.toString());

                sbd.setLength(0);
                iterator.remove();
                continue;
            }

            sbd.append(addSql)
                    .append(funcNum).append(", ")
                    .append("'").append(funcName).append("', ")
                    .append("'").append(next).append("'")
                    .append(");")
            ;
            sqlGroupMap.put(next, sbd.toString());
            sbd.setLength(0);
            iterator.remove();
        }

        if (sqlGroupMap.size() > 0) {
            sqlResultList.addAll(this.hashSql(sqlGroupMap));
            //把上一个菜单的分隔开
//                    sbd.append("\r\n");
            sqlResultList.add("\r\n\r\n");
            sqlGroupMap.clear();
        }

        //4、写入文本
        this.writerText(targetPath, sqlResultList);
    }

    public static void main(String[] args) {
        BopConfigFuncOracleTacleSqlInit sqlInit = new BopConfigFuncOracleTacleSqlInit();
        sqlInit.initSql(
                "D:\\LDtrader\\方正\\BOP\\菜单功能号整理\\3-funcOracleTable20220413.txt"
                , "D:\\LDtrader\\方正\\BOP\\菜单功能号整理\\5-funcOracleTableInitSql20220413.sql"
        );
    }
}
