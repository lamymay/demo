package com.arc.demo.test7.file2;

import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileComparator {
    public static void main(String[] args) {
        String sourceDirectory = "your_source_directory"; // 设置源目录
        String backupDirectory = "your_backup_directory"; // 设置备份目录

        // 构建文件比较器
        FileComparator comparator = new FileComparator();
        comparator.compareAndMoveFiles(sourceDirectory, backupDirectory);
    }

    // 比较文件并移动相同文件
    public void compareAndMoveFiles(String sourceDirectory, String backupDirectory) {
        try {
            // 构建目录对象
            File sourceDir = new File(sourceDirectory);
            File backupDir = new File(backupDirectory);

            // 创建备份目录
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // 使用哈希表来存储文件哈希值和最短文件名的映射
            Map<String, String> hashToShortestName = new HashMap<>();

            // 遍历源目录中的文件
            Files.walk(sourceDir.toPath())
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        File file = filePath.toFile();
                        String fileName = file.getName();

                        // 跳过无用的文件（特别小或者以.开头的隐藏文件）
                        if (file.length() < 100 || fileName.startsWith(".")) {
                            return;
                        }

                        // 计算文件的哈希值
                        try {
                            String hash = calculateSHA256(file);

                            // 如果哈希值已存在，比较文件名长度，保留较短的一个
                            if (hashToShortestName.containsKey(hash)) {
                                String existingShortestName = hashToShortestName.get(hash);
                                if (fileName.length() < existingShortestName.length()) {
                                    hashToShortestName.put(hash, fileName);
                                    // 移动较长的文件到备份目录
                                    moveFile(new File(sourceDirectory, existingShortestName), backupDir);
                                } else {
                                    // 移动当前文件到备份目录
                                    moveFile(file, backupDir);
                                }
                            } else {
                                hashToShortestName.put(hash, fileName);
                            }
                        } catch (IOException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 计算文件的SHA-256哈希值
    private String calculateSHA256(File file) throws IOException, NoSuchAlgorithmException {
        // 与需求1中的方法相同，略过以保持代码一致性
        // ...
    }

    // 移动文件到指定目录
    private void moveFile(File sourceFile, File targetDirectory) throws IOException {
        // 与需求1中的方法相同，略过以保持代码一致性
        // ...
    }
}
