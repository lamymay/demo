
package com.arc.demo.test7.file2;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

public class FileOrganizer {
    public static void main(String[] args) {
        String sourceDirectory = "your_source_directory"; // 设置源目录
        String tempDirectory = "your_temp_directory"; // 设置临时目录
        int maxFileSize = 500 * 1024 * 1024; // 设置最大文件大小，单位为字节
        int maxMemory = 10 * 1024; // 设置虚拟机最大内存，单位为MB

        // 设置虚拟机参数
        String[] jvmArgs = {"-Xmx" + maxMemory + "m"};

        // 构建文件组织器
        FileOrganizer organizer = new FileOrganizer();
        organizer.organizeFiles(sourceDirectory, tempDirectory, maxFileSize, jvmArgs);
    }

    // 计算文件的SHA-256哈希值
    private String calculateSHA256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    // 将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    // 移动文件到指定目录
    private void moveFile(File sourceFile, File targetDirectory) throws IOException {
        Path sourcePath = sourceFile.toPath();
        Path targetPath = Paths.get(targetDirectory.getAbsolutePath(), sourceFile.getName());
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    // 组织文件
    public void organizeFiles(String sourceDirectory, String tempDirectory, int maxFileSize, String[] jvmArgs) {
        try {
            // 设置虚拟机参数
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

            // 构建目录对象
            File sourceDir = new File(sourceDirectory);
            File tempDir = new File(tempDirectory);

            // 创建临时目录
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // 遍历源目录中的文件
            Files.walk(sourceDir.toPath())
                    .filter(Files::isRegularFile)
                    .parallel()
                    .forEach(filePath -> {
                        File file = filePath.toFile();
                        String fileName = file.getName();

                        // 跳过无用的文件（特别小或者以.开头的隐藏文件）
                        if (file.length() < 100 || fileName.startsWith(".")) {
                            return;
                        }

                        try {
                            // 计算文件的SHA-256哈希值
                            String hash = calculateSHA256(file);

                            // 如果文件大小超过指定大小，移动到临时目录
                            if (file.length() > maxFileSize) {
                                moveFile(file, tempDir);
                            } else {
                                // 构建新文件名
                                String newFileName = hash + "___" + fileName;

                                // 移动文件到新目录
                                moveFile(file, new File(sourceDirectory, newFileName));
                            }
                        } catch (IOException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
