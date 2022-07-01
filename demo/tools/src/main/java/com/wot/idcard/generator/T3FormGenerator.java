package com.wot.idcard.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * T3代码生成器
 */
public class T3FormGenerator {

    private final String txtPath;
    private final String targetPath;
    private final String author;
    private final Table tab;
    private final String projectPackage;
    public final Parameter STRINGP_ARAMETER = new Parameter("String");
    public static final String STR_LINE = "\n";

    public T3FormGenerator(String txtPath, String targetPath, String projectPackage, String author) {
        this.txtPath = txtPath;
        this.targetPath = targetPath;
        this.projectPackage = projectPackage;
        this.author = author;
        int lastIndexOf = projectPackage.lastIndexOf(".");
        String tabName = projectPackage.substring(lastIndexOf + 1);
        String impotPackage = projectPackage.substring(0, lastIndexOf);
//        tab = new Table( "TestForm", "com.fz.bop3.act.secu.form", "com.fz.bop3.pub.form.BaseForm");
        tab = new Table(tabName, impotPackage, "com.fz.bop3.pub.form.BaseForm");
//        tab.addSuperInterface(new Parameter("java.io.Serializable", "Serializable"));//增加实现
        tab.addIgnoreColumn("op_branch_no", "operator_no", "op_password", "op_station", "op_entrust_way", "position_str");
        tab.addPluginList(new SerializablePlugin());//增加序列化
        tab.addPluginList(new ToStringPlugin());//增加toString方法
        //获取表、字段
        this.introspectTables();
        //完善字段、方法、导入的包
        this.assessmentFieldMethod();
        //获取Java文件内容
        this.generateJavaFiles();
        //生成Java文件
        this.writeGeneratedJavaFile(tab, this.targetPath);
    }

    public static void main(String[] args) {
//        T3FormGenerator t3FormGenerator = new T3FormGenerator(
//                "C:\\Users\\luoding\\Desktop\\tmp\\a.txt",
////                "C:\\Users\\luoding\\Desktop\\tmp\\",
//                "D:\\workspaceFz\\itc-broker-fzbop3\\fzbop3\\fzbop-uf3-secu\\src\\main\\java",
//                "com.fz.bop3.act.secu.form.ACodeOpen",
//                "yaoql");
        T3FormGenerator t3FormGenerator = new T3FormGenerator(
                "C:\\Users\\luoding\\Desktop\\tmp\\postDataswapHolderOpenOut.txt",
//                "C:\\Users\\luoding\\Desktop\\tmp\\",
                "D:\\workspaceFz\\itc-broker-fzbop3\\fzbop3\\fzbop-uf3-secu\\src\\main\\java",
                "com.fz.bop3.act.secu.form.StockholderDataSwapOpen",
                "yaoql");
    }

    /**
     * 获取表、字段
     */
    public void introspectTables() {
        File file = new File(this.txtPath);
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
            while ((line = bf.readLine()) != null) {
                line = line.trim();
//                System.out.println(i + ">" + line);
                //功能中文	一码通账户开户		业务范围		更新日期	2021-07-29 10:53:59.0
                if (Pattern.matches("^功能名称.*", line)) {
                    String[] split = line.split("\t");
                    if (split.length <= 0) {
                        continue;
                    }
                    tab.t3Function = split[1];
                    tab.t3Service = split[4];
                }
                if (Pattern.matches("^功能中文.*", line)) {
                    String[] split = line.split("\t");
                    if (split.length <= 0) {
                        continue;
                    }
                    tab.t3FunctionName = split[1];
                }
                if (Pattern.matches("^[A-Za-z].*", line)) {
                    String[] split = line.split("\t");
                    if (split.length <= 0) {
                        continue;
                    }
                    String actualColumnName = split[0];
                    if (actualColumnName.length() <= 0 || !Pattern.matches("^[A-Za-z_0-9]*$", actualColumnName)) {
                        System.out.println(join("获取表、字段>忽略无效字段:", actualColumnName));
                        continue;
                    }
                    String documentDesc = join(split[2], "是".equals(split[3].trim()) ? "[必填]" : "");
                    String documentRemark = split.length == 7 ? split[6] : "";
                    if (documentRemark.length() > 1 && documentRemark.startsWith("\"")) {
                        documentRemark = documentRemark.substring(1);
                    }
                    tab.addColumn(new Column(actualColumnName, documentDesc, documentRemark));
                }
            }
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

    /**
     * 获取文件内容
     */
    private void generateJavaFiles() {
        tab.append("package ").append(tab.impotPackage).append(";");
        tab.append(STR_LINE).append(STR_LINE);
        if (!tab.importPackageSet.isEmpty()) {
            for (String importPackage : tab.importPackageSet) {
                tab.append("import ").append(importPackage).append(";").append(STR_LINE);
            }
            tab.append(STR_LINE);
        }
        for (String remark : tab.remarkList) {
            tab.append(remark).append(STR_LINE);
        }
        tab.append(JavaVisibility.PUBLIC.value).append("class ").append(tab.name);

        if (tab.superClass != null) {
            tab.append(" extends ").append(tab.superClass.shortPackageName);
        }

        if (!tab.superInterfaceList.isEmpty()) {
            tab.append(" implements ");
            Iterator<Parameter> iterator = tab.superInterfaceList.iterator();
            while (iterator.hasNext()) {
                Parameter superInterface = iterator.next();
                tab.append(superInterface.shortPackageName);
                if (iterator.hasNext()) {
                    tab.append(", ");
                }
            }
        }

        tab.append(" {");//开始
        tab.append(STR_LINE).append(STR_LINE);
        int indentLevel = 1;

        Iterator<Field> fieldIterator = tab.fieldList.iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            for (String remark : field.remarkList) {
                this.javaIndent(tab.javaContentSbd, indentLevel);
                tab.append(remark).append(STR_LINE);
            }
            this.javaIndent(tab.javaContentSbd, indentLevel);
            tab.append(JavaVisibility.PRIVATE.value);
            if (field.isStatic) {
                tab.append("static ");
            }
            if (field.isFinal) {
                tab.append("final ");
            }
            tab.append(field.type.shortPackageName).append(" ");
            tab.append(field.actualColumnName);
            if (field.initializationString != null && field.initializationString.length() > 0) {
                tab.append(" = ").append(field.initializationString);
            }
            tab.append(";");
            tab.append(STR_LINE);
            if (fieldIterator.hasNext()) {
                tab.append(STR_LINE);
            }
        }

        tab.append(STR_LINE);

        Iterator<Method> iterator = tab.methodList.iterator();
        while (iterator.hasNext()) {
            Method method = iterator.next();
            for (String remark : method.remarkList) {
                this.javaIndent(tab.javaContentSbd, indentLevel);
                tab.append(remark).append(STR_LINE);
            }

            if (method.annotations != null && !method.annotations.isEmpty()) {
                this.javaIndent(tab.javaContentSbd, indentLevel);
                for (String annotation : method.annotations) {
                    tab.append(annotation).append(STR_LINE);
                }
            }

            this.javaIndent(tab.javaContentSbd, indentLevel);
            tab.append(method.javaVisibility.value);
            if (method.returnType != null) {
                tab.append(method.returnType.shortPackageName);
            } else {
                tab.append("void");
            }
            tab.append(" ").append(method.name).append("(");
            if (!method.parameterList.isEmpty()) {
                for (Parameter parameter : method.parameterList) {
                    tab.append(parameter.shortPackageName).append(" ").append(parameter.name);
                }
            }
            tab.append(") {");//开始
            tab.append(STR_LINE);
            indentLevel++;
            for (String bodyLine : method.bodyLines) {
                this.javaIndent(tab.javaContentSbd, indentLevel);
                tab.append(bodyLine).append(STR_LINE);
            }
            indentLevel--;
//            tab.append(STR_LINE);
            this.javaIndent(tab.javaContentSbd, indentLevel);
            tab.append("}");//结束
            tab.append(STR_LINE);
            if (iterator.hasNext()) {
                tab.append(STR_LINE);
            }
        }
        tab.append(STR_LINE);
        tab.append("}");//结束
    }

    /**
     * 添加空格
     * @param sb 字符拼接
     * @param indentLevel 空格数量
     */
    public void javaIndent(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("    "); //$NON-NLS-1$
        }
    }

    /**
     * 完善字段、方法
     */
    private void assessmentFieldMethod() {
        if (tab.columnList.isEmpty()) {
            throw new RuntimeException("没有获取到字段");
        }
        //获取字段
        for (Column column : tab.columnList) {
            if (!tab.ignoreColumnSet.isEmpty() && tab.ignoreColumnSet.contains(column.actualColumnName)) {
                continue;
            }
            tab.addField(new Field(column));
        }
        if (tab.fieldList.isEmpty()) {
            throw new RuntimeException("没有可生成的字段");
        }
        tab.addRemark("/**");
        tab.addRemark(join(" * ", tab.t3FunctionName));
        tab.addRemark(join(" * ", tab.t3Function, " ", tab.t3Service));
        if (author != null) {
            tab.addRemark(join(" * @author ", author));
        }
        tab.addRemark(join(" * @Date ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        tab.addRemark(" */");

        for (Field field : tab.fieldList) {
            if (!tab.ignoreColumnSet.isEmpty() && tab.ignoreColumnSet.contains(field.actualColumnName)) {
                continue;
            }
            Parameter parameter = new Parameter(field.actualColumnName);
//            Field field = new Field(field);
            field.addRemark("/**");
//            if (field.documentRemark != null && field.documentRemark.length() > 50) {
//                field.addRemark(join(" * ", field.actualColumnName, " ", field.documentDesc, " ", field.documentRemark.substring(0, 50)));
//                field.addRemark(join(" * ", field.documentRemark.substring(50)));
//            } else {
            field.addRemark(join(" * ", field.actualColumnName, " ", field.documentDesc, " ", field.documentRemark));
//            }
            field/*.addRemark(" * ")*/.addRemark(" */")
            ;
//            tab.addField(field);

            //get方法
            Method getMethod = new Method(getGetterMethodName(field.actualColumnName), parameter);
            getMethod.addBodyLine(join("return ", field.actualColumnName, ";"));
            getMethod.addRemark("/**")
                    .addRemark(join(" * ", field.actualColumnName, " ", field.documentDesc))
                    .addRemark(" * ")
                    .addRemark(join(" * @return the value of ", field.actualColumnName, " ", field.documentDesc))
                    .addRemark(" */")
            ;
            tab.addMethod(getMethod);

            //set方法
            Method setMethod = new Method(getSetterMethodName(field.actualColumnName), null);
            setMethod.addParameter(parameter);
            setMethod.addBodyLine(join("this.", field.actualColumnName, " = ", field.actualColumnName, ";"));
            setMethod.addRemark("/**");
//            if (field.documentRemark != null && field.documentRemark.length() > 50) {
//                setMethod.addRemark(join(" * @param ", field.actualColumnName, " ", field.documentDesc, " ", field.documentRemark.substring(0, 50)));
//                setMethod.addRemark(join(" * ", field.documentRemark.substring(50)));
//            } else {
                setMethod.addRemark(join(" * @param ", field.actualColumnName, " ", field.documentDesc, " ", field.documentRemark));
//            }
            setMethod.addRemark(" * ")
                    .addRemark(" */")
            ;
            tab.addMethod(setMethod);

            //添加要增加的包名
            tab.addImportPackageSet(parameter.impotPackage);
            tab.addImportPackageSet(field.type.impotPackage);
        }

        if (!tab.pluginList.isEmpty()) {
            for (Plugin plugin : tab.pluginList) {
                plugin.markPlugin(tab);
            }
        }
    }

    /**
     * 获取get方法名
     * @param property 字段名
     * @return get方法名
     */
    public String getGetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        sb.insert(0, "get"); //$NON-NLS-1$
        return sb.toString();
    }


    /**
     * 获取set方法名
     * @param property 字段名
     * @return set方法名
     */
    public String getSetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }
        sb.insert(0, "set"); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * 拼接字符串
     * @param str 字符串数组
     * @return 字符串
     */
    private String join(String ... str) {
        StringBuilder sbd = new StringBuilder();
        for (String s : str) {
            if (s == null || s.length() <= 0) {
                continue;
            }
            sbd.append(s);
        }
        return sbd.toString();
    }

    /**
     * 生成Java文件
     * @param tab
     */
    private void writeGeneratedJavaFile(Table tab, String parentPath) {
        if (parentPath == null || parentPath.length() <= 0) {
            throw new RuntimeException("生成文件的存储路径设置错误");
        }
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.isDirectory()) {
            throw new RuntimeException("生成文件的存储路径设置错误");
        }
        StringBuilder sbd = new StringBuilder();
        StringTokenizer st = new StringTokenizer(tab.impotPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sbd.append(st.nextToken());
            sbd.append(File.separatorChar);
        }
        sbd.append(tab.getFileName());
        file = new File(file, sbd.toString());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            this.writeFile(file, tab.getContent(), "UTF-8");
            System.out.println(join("生成java文件成功，文件路径>", file.getAbsolutePath()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException(join("生成Java文件异常", ioException.getMessage()));
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file.
     * @param file 文件
     * @param content 文件内容
     * @param fileEncoding 编码级
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    /** 表 **/
    public class Table extends BaseIntrospect {
        private final String impotPackage;
        private final String name;
        private String t3Function;//方法名
        private String t3FunctionName;//方法名中文
        private String t3Service;//服务名
        private final StringBuilder javaContentSbd;//实体内容
        private final List<Column> columnList;//字段
        private final List<Field> fieldList;//字段
        private final List<Method> methodList;//方法
        private final TreeSet<String> importPackageSet;//方法
        private final Parameter superClass;//父类
        private final List<Parameter> superInterfaceList;//实现
        private final List<Plugin> pluginList;//工具
        private final Set<String> ignoreColumnSet;//忽略的字段，不做显示，且toString中也没有
        public Table(String name, String impotPackage) {
            this(name, impotPackage, null);
        }
        public Table(String name, String impotPackage, String superClass) {
            this.name = name;
            this.impotPackage = impotPackage;
            this.javaContentSbd = new StringBuilder();
            this.columnList = new ArrayList<>();
            this.fieldList = new ArrayList<>();
            this.methodList = new ArrayList<>();
            this.importPackageSet = new TreeSet<>();
//            this.superClass = new Parameter("com.fz.bop3.pub.form.BaseForm", "BaseForm");
            this.superInterfaceList = new ArrayList<>();
            this.pluginList = new ArrayList<>();
            this.ignoreColumnSet = new HashSet<>();
            if (superClass != null) {
                this.superClass = new Parameter(superClass, superClass.substring(superClass.lastIndexOf(".") + 1));
                this.addImportPackageSet(this.superClass.impotPackage);
            } else {
                this.superClass = null;
            }
        }
        public void addSuperInterface(Parameter parameter) {
            if (parameter == null || parameter.name == null || parameter.name.length() <= 0) {
                return;
            }
            if (!this.superInterfaceList.isEmpty()) {
                for (Parameter oldParameter : this.superInterfaceList) {
                    if (oldParameter.name.equals(parameter.name)) {
                        return;
                    }
                }
            }
            this.superInterfaceList.add(parameter);
            this.addImportPackageSet(parameter.impotPackage);
        }
        public void addColumn(Column column) {
            this.columnList.add(column);
        }
        public void addField(int index, Field field) {
            this.fieldList.add(index, field);
        }
        public void addField(Field field) {
            this.fieldList.add(field);
        }
        public void addMethod(Method method) {
            this.methodList.add(method);
        }
        public void addMethod(int index, Method method) {
            this.methodList.add(index, method);
        }
        public void addImportPackageSet(String importPackage) {
            if (importPackage.length() <= 0 || importPackage.startsWith("java.lang")) {
                return;
            }
            this.importPackageSet.add(importPackage);
        }
        public Table append(String str) {
            this.javaContentSbd.append(str);
            return this;
        }
        public String getContent() {
            return this.javaContentSbd.toString();
        }
        public void addPluginList(Plugin plugin) {
            this.pluginList.add(plugin);
        }
        public void addIgnoreColumn(String ... columnNames) {
            for (String columnName : columnNames) {
                this.ignoreColumnSet.add(columnName);
            }
        }
        public String getFileName() {
            return join(this.name, ".java");
        }
    }
    /** 原始字段 **/
    public class Column extends BaseIntrospect {
        protected final String actualColumnName;//字段
        protected final String documentDesc;//文档上说明、必填
        protected final String documentRemark;//文档上备注
        public Column(String actualColumnName, String documentDesc, String documentRemark) {
            this.actualColumnName = actualColumnName;
            this.documentDesc = documentDesc;
            this.documentRemark = documentRemark;
        }
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Column{");
            sb.append("actualColumnName='").append(actualColumnName).append('\'');
            sb.append(", documentDesc='").append(documentDesc).append('\'');
            sb.append(", documentRemark='").append(documentRemark).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
    /** 原始字段 **/
    public class Field extends Column {
        private final Parameter type;
        private String initializationString;//默认值
        private boolean isStatic;
        private boolean isFinal;
        public Field(Column column) {
            this(column.actualColumnName, column.documentDesc, column.documentRemark);
        }
        public Field(String actualColumnName, String documentDesc, String documentRemark) {
            this(STRINGP_ARAMETER, actualColumnName, documentDesc, documentRemark);
        }
        public Field(Parameter type, String actualColumnName, String documentDesc, String documentRemark) {
            super(actualColumnName, documentDesc, documentRemark);
            this.type = type;
        }
    }
    /**方法**/
    public class Method extends BaseIntrospect {
        private final String name;
//        private final JavaVisibility javaVisibility;
        private final Parameter returnType;
        private final List<Parameter> parameterList;
        private final List<String> bodyLines;//方法内容
        public Method(String name, Parameter returnType) {
            this(name, JavaVisibility.PUBLIC, returnType);
        }
        public Method(String name, JavaVisibility javaVisibility, Parameter returnType) {
            this.name = name;
            this.javaVisibility = javaVisibility;
            this.returnType = returnType;
            this.parameterList = new ArrayList<>();
            this.bodyLines = new ArrayList<>();
        }
        public void addParameter(Parameter parameter) {
            this.parameterList.add(parameter);
        }
        public void addBodyLine(String bodyLine) {
            this.bodyLines.add(bodyLine);
        }
    }
    /**参数**/
    public class Parameter {
        private final String impotPackage;
        private final String name;
        private final String shortPackageName;
        public Parameter(String name) {
            this("java.lang.String", name, "String");
        }
        public Parameter(String impotPackage, String name) {
            this(impotPackage, name, impotPackage.substring(impotPackage.lastIndexOf(".") + 1));
        }
        public Parameter(String impotPackage, String name, String shortPackageName) {
            this.impotPackage = impotPackage;
            this.name = name;
            this.shortPackageName = shortPackageName;
        }
    }
    /** 插件 **/
    public abstract class Plugin {
        public abstract void markPlugin(Table tab);
    }
    /** 序列化插件 **/
    public class SerializablePlugin extends Plugin {
        private final Parameter serializable;
        public SerializablePlugin() {
            this.serializable = new Parameter("java.io.Serializable", "Serializable");
        }
        @Override
        public void markPlugin(Table tab) {
            tab.addSuperInterface(this.serializable);
            Field serialVersionUID = new Field(serializable, "serialVersionUID", null, null);
            serialVersionUID.isFinal = true;
            serialVersionUID.isStatic = true;
            serialVersionUID.initializationString = "1L";
            tab.addField(0, serialVersionUID);
        }
    }
    /** toString插件 **/
    public class ToStringPlugin extends Plugin {
        @Override
        public void markPlugin(Table tab) {
            Method method = new Method("toString", STRINGP_ARAMETER);
            method.addBodyLine("final StringBuilder sb = new StringBuilder();");
            method.addBodyLine("sb.append(getClass().getSimpleName());");
            method.addBodyLine("sb.append(\" {\");");//开始
            StringBuilder sb = new StringBuilder();
            Iterator<Field> iterator = tab.fieldList.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                if (field.isFinal) {
                    continue;
                }
                sb.append("sb.append(\"").append(", ").append(field.actualColumnName) //$NON-NLS-1$ //$NON-NLS-2$
                        .append("=\")").append(".append(").append(field.actualColumnName) //$NON-NLS-1$ //$NON-NLS-2$
                        .append(");"); //$NON-NLS-1$
                method.addBodyLine(sb.toString());
                if (iterator.hasNext()) {
                    sb.setLength(0);
                }
            }
            method.addBodyLine("sb.append('}');");//结束
            method.addBodyLine("return sb.toString();");
            method.addAnnotation("@Override");
            tab.addMethod(tab.methodList.size(), method);
        }
    }
    public enum JavaVisibility {
        PUBLIC("public "), //$NON-NLS-1$
        PRIVATE("private "), //$NON-NLS-1$
        PROTECTED("protected "), //$NON-NLS-1$
        DEFAULT(""); //$NON-NLS-1$
        private final String value;
        private JavaVisibility(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    public abstract class BaseIntrospect {
        protected JavaVisibility javaVisibility = JavaVisibility.DEFAULT;
        protected List<String> annotations;
        protected final List<String> remarkList = new ArrayList<>();//类备注
        /** 按行添加备注，最终一行一行按顺序输出 **/
        public BaseIntrospect addRemark(String... remarks) {
            if (remarks.length > 0) {
                for (String remark : remarks) {
                    this.remarkList.add(remark);
                }
            }
            return this;
        }
        public void addAnnotation(String annotation) {
            if (annotation == null || annotation.length() <= 0) {
                return;
            }
            if (this.annotations == null) {
                this.annotations = new ArrayList<>();
            }
            this.annotations.add(annotation);
        }
    }

}
