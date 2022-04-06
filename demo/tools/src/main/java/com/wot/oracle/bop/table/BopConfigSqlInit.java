package com.wot.oracle.bop.table;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 表结构
 * `db_pub`
 * DROP TABLE IF EXISTS `bop_config_yql`;
 * CREATE TABLE bop_config_yql(
 *   `row_id` int(11) NOT NULL AUTO_INCREMENT,
 *   `oracle_menu_id` int(11) NOT NULL,
 *   `oracle_menu_caption` varchar(100) NOT NULL,
 *   `func_num` int(11) NOT NULL,
 *   `func_name` varchar(100) NOT NULL,
 *   PRIMARY KEY (`row_id`) USING BTREE
 * ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
 */
public class BopConfigSqlInit {

    /** sql注释前缀 */
    public static final String sqlNotes = "-- ";
    private static final String tableSql = "DROP TABLE IF EXISTS `db_pub`.`bop_config_yql`;\n" +
            "CREATE TABLE `db_pub`.bop_config_yql(\n" +
            "  `row_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `oracle_menu_id` int(11) NOT NULL,\n" +
            "  `oracle_menu_caption` varchar(100) NOT NULL,\n" +
            "  `func_num` int(11) NOT NULL,\n" +
            "  `func_name` varchar(100) NOT NULL,\n" +
            "  PRIMARY KEY (`row_id`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;";
    /** 新增原始sql */
    private static final String addSql = "INSERT INTO `db_pub`.`bop_config_yql` (`oracle_menu_id`, `oracle_menu_caption`, `func_num`, `func_name`) VALUES (";

    /**
     * 按行读取功能号和描述
     * @param filePath 文件路径
     */
    public List<String> readBopConfigTxt(String filePath) {
        if (filePath == null || filePath.length() <= 0) {
            throw new RuntimeException("文件目录为空");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件目录为空");
        }

        FileReader fr = null;
        BufferedReader bf = null;
        try {
            List<String> resultList = new ArrayList<>();
            fr = new FileReader(file);
            bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                //过滤
                if (str == null || str.length() <= 0) {
                    continue;
                }
                resultList.add(str);
            }
            System.out.println("读取文件结束," + filePath + ", size:" + resultList.size());
            return resultList;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("读取文件异常," + filePath, ioException.getCause());
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
//        throw new RuntimeException("读取文件异常," + filePath);
    }

    /**
     * 分割功能号和名称
     * (170002)LS_BOP账户管理_客户信息查询
     * @param str 名称
     */
    public String[] splitFuncNumAndName(String str) {
        if (str == null) {
            throw new RuntimeException("分割功能号和名称,字符串为空");
        }
        str = str.trim();
        if (str.length() <= 0) {
            throw new RuntimeException("分割功能号和名称,字符串为空");
        }
//        String[] split = str.split("\\)");
//        if (split == null || split.length != 2) {
//            throw new RuntimeException("分割功能号和名称异常," + str);
//        }
//        String funcNum = split[0];
//        if (!funcNum.startsWith("(")) {
//            throw new RuntimeException("分割功能号和名称异常," + str);
//        }
//        split[0] = funcNum.substring(1);
        String[] split = new String[2];
        split[0] = str.substring(1, str.indexOf(")"));
        split[1] = str.substring(str.indexOf(")")  + 1);
        return split;
    }

    /**
     * 分割菜单编号和名称
     * 股票期权一站式开户(73070104)
     * @param str 股票期权一站式开户(73070104)
     */
    public String[] splitOracleMenuIdAndName(String str) {
        if (str == null) {
            throw new RuntimeException("分割菜单编号和名称,字符串为空");
        }
        str = str.trim();
        if (str.length() <= 0) {
            throw new RuntimeException("分割菜单编号和名称,字符串为空");
        }
        String[] split = str.split("\\(");
        if (split == null || split.length != 2) {
            throw new RuntimeException("分割菜单编号和名称异常," + str);
        }
        String menuName = split[1];
        if (!menuName.endsWith(")")) {
            throw new RuntimeException("分割菜单编号和名称异常," + str);
        }
        split[1] = menuName.substring(0, menuName.length() - 1);
        return split;
    }

    /**
     * 写入sql到目标文本
     * @param targetPath 目标文本所在目录
     * @param sqlList sql集合
     */
    public void writerText(String targetPath, List<String> sqlList) {
        BufferedWriter writer = null;
        int total = 0;
        try {
            File file = new File(targetPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (!file.isFile()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            Iterator<String> iterator = sqlList.iterator();
            while (iterator.hasNext()) {
                writer.write(iterator.next());
                writer.newLine();//换行
                total++;
                iterator.remove();
            }
            writer.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("写入初始化sql语句异常" + targetPath, ioException.getCause());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            System.out.println("写入初始化sql语句结束,写入总量:" + total + "," + targetPath);
        }
    }

    /**
     * 按编号排序去重
     * @param sqlGroupfuncNumMap 当前菜单 sqlMap
     */
    public List<String> hashSql(Map<String, String> sqlGroupfuncNumMap) {
        //3、按编号排序去重
        List<String> keyList = new ArrayList<>(sqlGroupfuncNumMap.keySet());
        Collections.sort(keyList);

        List<String> sqlList = new ArrayList<>(keyList.size());
        Iterator<String> keyIterator = keyList.iterator();
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();
            sqlList.add(sqlGroupfuncNumMap.get(next));
            keyIterator.remove();
        }
        return sqlList;
    }

    /**
     * 初始化sql脚本
     * @param filePath 文件路径
     * @param targetPath 目标文本所在目录
//     * @param oracleMenuId 菜单编号
//     * @param oracleMenuCaption 菜单描述
     */
    private void initSql(String filePath, String targetPath/*, String oracleMenuId, String oracleMenuCaption*/) {
        //1、读取
        List<String> funcNumAndNameList = this.readBopConfigTxt(filePath);

        Map<String, String> sqlGroupfuncNumMap = new HashMap<>(funcNumAndNameList.size());
        List<String> sqlResultList = new ArrayList<>(funcNumAndNameList.size());
        sqlResultList.add(tableSql);
        sqlResultList.add("\r\n\r\n");

        //2、生成sql
        Iterator<String> iterator = funcNumAndNameList.iterator();
        StringBuilder sbd = new StringBuilder();
        String oracleMenuId = "", oracleMenuCaption = "";
        while (iterator.hasNext()) {
            String next = iterator.next();

            //菜单开头，表示上一个结束
            if (!next.startsWith("(")) {
//                String[] oracleMenuIdAndName = this.splitOracleMenuIdAndName(next);
                if (sqlGroupfuncNumMap.size() > 0) {
                    sqlResultList.addAll(this.hashSql(sqlGroupfuncNumMap));
                    //把上一个菜单的分隔开
//                    sbd.append("\r\n");
                    sqlResultList.add("\r\n\r\n");
                    sqlGroupfuncNumMap.clear();
                }
                String[] oracleMenuIdAndName = this.splitOracleMenuIdAndName(next);
                oracleMenuId = oracleMenuIdAndName[1];
                oracleMenuCaption = oracleMenuIdAndName[0];
                //添加描述信息
                sbd.append(sqlNotes).append(next);
                sqlResultList.add(sbd.toString());
                sbd.setLength(0);
                iterator.remove();
                continue;
            }
            String[] funcNumAndNameArrays = this.splitFuncNumAndName(next);
//            funcNumAndNameArrays[0]

            sbd.append(addSql)
                    .append(oracleMenuId).append(", ")
                    .append("'").append(oracleMenuCaption).append("', ")
                    .append(funcNumAndNameArrays[0]).append(", ")
                    .append("'").append(funcNumAndNameArrays[1]).append("'")
                    .append(");")
            ;


            sqlGroupfuncNumMap.put(funcNumAndNameArrays[0], sbd.toString());
            sbd.setLength(0);
            iterator.remove();
        }

        if (sqlGroupfuncNumMap.size() > 0) {
            sqlResultList.addAll(this.hashSql(sqlGroupfuncNumMap));
            //把上一个菜单的分隔开
//                    sbd.append("\r\n");
            sqlResultList.add("\r\n\r\n");
            sqlGroupfuncNumMap.clear();
        }

//        //3、按编号排序去重
//        List<String> keyList = new ArrayList<>(sqlGroupfuncNumMap.keySet());
//        Collections.sort(keyList);
//
//        List<String> sqlList = new ArrayList<>(keyList.size());
//        Iterator<String> keyIterator = keyList.iterator();
//        while (keyIterator.hasNext()) {
//            String next = keyIterator.next();
//            sqlList.add(sqlGroupfuncNumMap.get(next));
//            keyIterator.remove();
//        }

        //4、写入文本
        this.writerText(targetPath, sqlResultList);
    }

    public static void main(String[] args) {
        BopConfigSqlInit init = new BopConfigSqlInit();
//        System.out.println(Arrays.toString(init.splitFuncNumAndName("(170002)LS_BOP账户管理_客户信息查询")));
//        System.out.println(Arrays.toString(init.splitFuncNumAndName("(460051)LS_统计(账户)_股票质押风险名单客户查询")));
        init.initSql("D:\\LDtrader\\方正\\BOP\\菜单功能号整理\\1-menuInit.txt"
                , "D:\\LDtrader\\方正\\BOP\\菜单功能号整理\\2-menuInitSql.sql"
                /*, "", "证券账户开户"*/);
    }

}
