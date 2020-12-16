package test;

import org.apache.commons.collections4.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CopyRight工具类  * @author wangguodong  * @email wangguodong@veredholdings.com
 */
public final class CopyrightUtil {
    private CopyrightUtil() {
    }

    public static void main(String[] args) throws Exception {
        addCopyRight2JavaFile("D:\\vered_workspace\\bigtree\\bigtree-parent\\bigtree-base\\bigtree-utils\\src\\main\\java\\com\\bigtreefinance\\utlis\\CollectionUtil.java", getCopyRightMap());
    }

    /**
     * * @param path 文件路径  * @param copyRightContent copyright模板  * @throws Exception
     */
    public static void addCopyRight2JavaFile(String path, List<String> copyRightContent) throws Exception {
        File file = new File(path);
        handle(file, copyRightContent);
    }

    private static void handle(File file, List<String> copyRightContent) throws Exception {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isFile()) {
                        reWriteCopyRight(f, copyRightContent);
                    } else {
                        handle(f, copyRightContent);
                    }
                }
            }
        } else if (file.isFile()) {
            reWriteCopyRight(file, copyRightContent);
        }
    }

    public static void reWriteCopyRight(File file, List<String> copyRightContent) throws Exception {
        String fileName = file.getName();
        if (checkIsJavaFile(fileName)) {
            List<String> contents = FileUtil.readLines(file, "UTF-8");
            if (CollectionUtils.isNotEmpty(contents)) {
                int index = 0;
                for (int i = 0; i < contents.size(); i++) {
                    String line = contents.get(i);
                    if (line.startsWith("package")) {
                        index = i;
                        break;
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
                    for (String copyRightStr : copyRightContent) {
                        writer.write(copyRightStr + "\r\n");
                    }
                    for (int i = index; i < contents.size(); i++) {
                        writer.write(contents.get(i) + "\r\n");
                    }
                }
            }
        }
    }

    private static boolean checkIsJavaFile(String fileName) {
        return fileName.endsWith(".java");
    }

    public static List<String> getCopyRightMap() {
        List<String> copyRightContent = new ArrayList<>();
        copyRightContent.add("/*");
        copyRightContent.add(" */");
        return copyRightContent;
    }
}