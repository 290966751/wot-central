package com.wot.generator.api;

import com.wot.generator.config.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 代码生成器
 */
public class WotGenerator {

    /** The Class Context. **/
    private final Context context;

    public WotGenerator(Context context) {
        this.context = context;
    }

    /** The generated java files. */
    private List<GeneratedJavaFile> generatedJavaFiles;

    /**
     * 代码生成
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void generate() throws IOException {
        //1、获取表、字段信息
        this.context.introspectTables();
        //2、生成Java、Xml内容
        //3、写文件
        this.writeGeneratedJavaFile(this.generatedJavaFiles);
    }

    /**
     * 写Java文件
     * @param generatedJavaFiles The generated java files.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeGeneratedJavaFile(List<GeneratedJavaFile> generatedJavaFiles) throws IOException {
        if (generatedJavaFiles == null || generatedJavaFiles.isEmpty()) {
            return;
        }
        for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
            //TODO
            this.writeFile(new File(generatedJavaFile.getFileName()), generatedJavaFile.getFormattedContent(), generatedJavaFile.getFileEncoding());
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

}
