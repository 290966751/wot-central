package com.wot.generator.api;

/**
 * Abstract class that holds information common to all generated files.
 */
public abstract class GeneratedFile {

    /** The target project. */
    protected final String targetProject;

    /** The file encoding. */
    protected final String fileEncoding;

    /**
     * Instantiates a new generated file.
     * @param targetProject The target project
     */
    public GeneratedFile(String targetProject) {
        this(targetProject, "UTF-8");
    }

    /**
     * Instantiates a new generated file.
     * @param targetProject The target project
     * @param fileEncoding 文件编码级
     */
    public GeneratedFile(String targetProject, String fileEncoding) {
        this.targetProject = targetProject;
        this.fileEncoding = fileEncoding != null && fileEncoding.length() > 0 ? fileEncoding : "UTF-8";
    }

    /**
     * 获取目标文件
     * @return the target project
     */
    public String getTargetProject() {
        return targetProject;
    }

    /**
     * 获取文件编码级
     * @return 文件编码级
     */
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * 获取文件内容
     * @return 文件内容
     */
    public abstract String getFormattedContent();

    /**
     * 获取文件名称
     * @return
     */
    public abstract String getFileName();

}
