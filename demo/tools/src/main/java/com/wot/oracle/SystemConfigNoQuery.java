package com.wot.oracle;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Oracle配置查询
 */
public class SystemConfigNoQuery {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    /**
     * 要执行的sql
     */
    private static final String SQL = "SELECT config_no, concat(concat(config_no, '-'),config_name) AS name_ FROM hs_user.sysconfig WHERE config_no IN";
    /**
     * 驱动<br>
     * 多数据源公用一个驱动
     */
    private static Driver driver;
    /**
     * 驱动所在包路径，多个
     */
    private static final List<String> entries = new ArrayList<>();
    /**
     * oracle驱动所在jar
     */
    private static final String oracleJAR = "D:\\workspaceLd\\git\\wot-central\\demo\\tools\\src\\main\\resources\\oracle-driver-ojdbc6-12.1.0.1.jar";

    public SystemConfigNoQuery() {
        //加载配置的jar路径
        entries.add(oracleJAR);
        //加载jar，加载驱动
        this.driver = this.getDriver();
    }

    public static void main(String[] args) {
        SystemConfigNoQuery systemConfigNoQuery = new SystemConfigNoQuery();
//        String str = "70038,70539,2184,2384,70377,2631,70638,70169,2549,2108,70028,70038,70539,2184,2384,70377,70133,1100,1113,1189,5186";
//        String str = "36047,36017,2334,2617,70444,36050,36040,70040,70041,70087,70460,70483,36034,36031,36021,36014,36015,36016,36022,36023,36024,36047";
//        String str = "1255,1100,70513,1263,1271,1277,70029,70169,70346,70453,70041,70077,70096,70003,36017,2334,36029,2617,70444,36050,36040,36021,70460,70483,70021,70081,70060,70329,70496,70095,70099,36045,36039,70018,2631,1264,1263,1271,1277,70000,1127,1128,1322,1335,70143,70020,70062,2001,36009,36027,36005,70162,70094,36902,1114,70480,70483,70571,1292,1160,70633,70040,70041,70087,36034,36031,36014,36015,36016,36022,36023,36024,36025";
//        String str = "70346,70453,70460,70483,70095,70099,70041,70077,70096,70003,36017,2334,36029,2617,70444,36050,36040,70021,70081,36019,36031,36021,36014,36015,36016,36022,36023,36024,36025,36045";
//        String str = "1100,70101,31129,31123,70049,70364,70125";
//        String str = "31909,70038,70539,2184,2384,70377,2631,70169,2549,2108,70028 ,70133,70539,2184,2384,70377,70133,70038,14036,70402,70491";
//        String str = "70036,1100,1255,70584,70102,70029,70169,31076,70431,70435,14000,36005,31123,14000,31107,70019,70107,1115,70083,70155,70102,70036,70110,70094,3180,70396,1127,1128,1335,14082,70404,70447,70595,1160,2001,1204,1248,70358,1255,2462,1271,1276,14060,14053,70209,2563,2389,1292,1114,70408,70049,2455,70437,70438,1247";
//        String str = "70642,70652,1250,2068,2044,1207,1100,1133,14024,2273,2380,2252,70313,3185,1411,4140,70420";//证券账户修改
//        String str = "4102,1264,70144,70228,70639,4132,4142,4146";//证券账户销户
//        String str = "1209,70601,2068,2046,1270,4173,172,70382,70234,70098,70128,1139,1271,2377,70147,2044,2045,2068,70160,2527,70135,70395,70401,2557,70578,70587,70649,70523,2631,4166,1204,1292,70321,1100";//证券账户开户
//        String str = "2024,1100,2295,2352,1263,2241,2182,70037,1255,2149,2184,2325,70470";//港股通开户
        String str = "4144,2631";//港股通开户

        String[] configNos = systemConfigNoQuery.getConfigNos(str);
        Map<String, String> systemConfigNo = systemConfigNoQuery.querySystemConfigNo(systemConfigNoQuery.getConnection(
                ""
                , ""
                , ""
                ), configNos);
        Map<String, String> systemConfigNo2 = systemConfigNoQuery.querySystemConfigNo(systemConfigNoQuery.getConnection(
                ""
                , ""
                , ""
        ), configNos);
        systemConfigNo.putAll(systemConfigNo2);
        for (String configNo : configNos) {
            if (systemConfigNo.containsKey(configNo)) {
                continue;
            }
            systemConfigNo.put(configNo, new StringBuilder().append(configNo).append("-").toString());
        }
        List<String> list = new ArrayList<>(systemConfigNo.values());
        System.out.println("--------result--------");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                boolean b = o1.endsWith("-");
                boolean b2 = o2.endsWith("-");
                if (b && b2) {
                   return o1.compareTo(o2) > 0 ? 1 : -1;
                }
                if (!b && b2) {
                    return -1;
                }
                if (b && !b2) {
                    return 1;
                }
                return o1.compareTo(o2) > 0 ? 1 : -1;
            }
        });
//        System.out.println(list.toString());

//        StringBuilder sbd = new StringBuilder();
        for (String config : list) {
            System.out.println(config);
//            sbd.append(config).append("\r");
        }
//        System.out.println(sbd.toString());
    }

    /**
     * 获取他要查询的配置编号
     */
    public String[] getConfigNos(String configNoStr) {
        String[] configNos = configNoStr.split(",");
        System.out.println("query nos size:" + configNos.length);
        return configNos;
    }

    /**
     * 查询配置数据，默认编号-编号名称
     */
    public Map<String, String> querySystemConfigNo(Connection connection, String[] configNos) {
        Statement statement = null;
        ResultSet resultSet = null;
//        Set<String> set = new HashSet<>(100);
        Map<String, String> resultParams = new HashMap<>(100);
        try {
            statement = connection.createStatement();
            StringBuilder sbd = new StringBuilder(SQL).append(" (");
//            String[] configNos = configNoStr.split(",");
//            System.out.println("query nos size:" + configNos.length);
            for (int i = 0, lenth = configNos.length, max = lenth - 1; i < lenth; i++) {
                String configNo = configNos[i];
                if (configNo == null || configNo.length() <= 0) {
                    continue;
                }
                sbd.append("'").append(configNo).append("'");
                if (i != max) {
                    sbd.append(",");
                }
            }
            sbd.append(")");
            System.out.println("sql:\n" + sbd.toString());
            resultSet = statement.executeQuery(sbd.toString());
            while (resultSet.next()) {
                String config_no = resultSet.getString("config_no");
                String name_ = resultSet.getString("name_");
//                set.add(resultSet.getString(1));
                resultParams.put(config_no, name_);
            }
//            System.out.println("query nos result:" + set.size());
            return resultParams;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            this.closeAll(connection, statement, resultSet);
            System.out.println("query result size:" + resultParams.size());
        }
        return resultParams;
    }

    /**
     * 加载jar包，获取ClassLoader
     */
    public ClassLoader getCustomClassloader(List<String> entries) {
        if (entries == null) {
            throw new RuntimeException("没有可用jar"); //$NON-NLS-1$
        }
        List<URL> urls = new ArrayList<>(entries.size());
        File file;
        for (String classPathEntry : entries) {
            file = new File(classPathEntry);
            if (!file.exists()) {
                throw new RuntimeException("文件不存在, file Path:" + classPathEntry); //$NON-NLS-1$
            }
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("加载class异常, file Path:" + classPathEntry); //$NON-NLS-1$
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
    }

    /**
     * 获取ORACLE驱动
     */
    private Driver getDriver() {
        Driver driver = null;
        try {
            ClassLoader classloader = this.getCustomClassloader(entries);
            Class<?> clazz = Class.forName(DRIVER, true, classloader);
            driver = (Driver) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("get Driveri 异常");
        }
        return driver;
    }

    /**
     * 获取连接
     */
    public Connection getConnection(String url, String userName, String pwd) {
        Connection connection = null;
        try {
//            Class.forName(driver);
//            connection = DriverManager.getConnection(url, userName, pwd);
            if (driver != null) {
//            Driver driver = getDriver();
                driver = getDriver();;
            }
            Properties props = new Properties();
            if (userName != null && userName.length() > 0) {
                props.setProperty("user", userName); //$NON-NLS-1$
            }
            if (pwd != null && pwd.length() > 0) {
                props.setProperty("password", pwd); //$NON-NLS-1$
            }
//            props.putAll(otherProperties);
            connection = driver.connect(url, props);
            System.out.println("获取连接成功, " + url + "," + userName + "/" + pwd);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    /**
     * 释放所有连接
     */
    public void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
