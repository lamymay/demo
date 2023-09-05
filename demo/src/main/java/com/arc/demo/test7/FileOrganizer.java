
package com.arc.demo.test7;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class FileOrganizer {
    public static void main(String[] args) {
        String sourceDirectory = "your_source_directory"; // 设置源目录
        String tempDirectory = "your_temp_directory"; // 设置临时目录
        String backupDirectory = "your_backup_directory"; // 设置备份目录
        int maxFileSize = 500 * 1024 * 1024; // 设置最大文件大小，单位为字节
        int maxMemory = 10 * 1024; // 设置虚拟机最大内存，单位为MB

        // 设置虚拟机参数
        String[] jvmArgs = {"-Xmx" + maxMemory + "m"};

        // 构建文件组织器
        FileOrganizer organizer = new FileOrganizer();
        organizer.organizeFiles(sourceDirectory, tempDirectory, backupDirectory, maxFileSize, jvmArgs);
    }

    // 计算文件的SHA-256哈希值
    private String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
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
    public void organizeFiles(String sourceDirectory, String tempDirectory, String backupDirectory, int maxFileSize, String[] jvmArgs) {
        try {
            // 设置虚拟机参数
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

            // 构建目录对象
            File sourceDir = new File(sourceDirectory);
            File tempDir = new File(tempDirectory);
            File backupDir = new File(backupDirectory);

            // 创建临时目录
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // 创建备份目录
            if (!backupDir.exists()) {
                backupDir.mkdirs();
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
                            // 计算文件的哈希值
                            String hash = calculateHash(file);

                            // 构建新文件名
                            String newFileName = hash + "___" + fileName;

                            // 如果文件大小超过指定大小，移动到临时目录
                            if (file.length() > maxFileSize) {
                                moveFile(file, tempDir);
                            } else {
                                // 如果文件已经存在于备份目录，比较文件名长度，保留较短的一个
                                File[] existingFiles = backupDir.listFiles((dir, name) -> name.startsWith(hash + "___"));
                                if (existingFiles != null && existingFiles.length > 0) {
                                    for (File existingFile : existingFiles) {
                                        if (existingFile.length() == file.length() && existingFile.getName().length() < newFileName.length()) {
                                            moveFile(file, backupDir);
                                            return;
                                        }
                                    }
                                }

                                // 移动文件到备份目录
                                moveFile(file, backupDir);
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

//我了解到有CRC32 方式快速计算文件相同与否 和我们用的计算有什么区别
//
//ChatGPT
//CRC32（Cyclic Redundancy Check）是一种循环冗余校验算法，通常用于检查数据传输的完整性。CRC32的主要目标是快速检测数据是否发生了错误，而不是计算数据的哈希值。因此，CRC32算法不适合用于计算文件的哈希值或用于确定文件内容是否相同，因为它不足以提供唯一性和碰撞避免的保证。
//
//与CRC32不同，SHA-256（Secure Hash Algorithm 256-bit）是一种密码学安全的哈希算法，它的主要用途是生成数据的哈希值，以确保数据的完整性和安全性。SHA-256生成的哈希值在理论上应该是唯一的，并且能够高度保证不同数据产生不同的哈希值，从而适合用于文件完整性验证和文件内容的比较。
//
//在文件整理和文件比较的场景中，使用SHA-256或类似的密码学哈希算法更为合适，因为它们提供了更高的哈希唯一性和安全性。CRC32主要用于快速检测数据传输中的错误，而不是用于数据的唯一性验证。
//
//如果你只需要快速检测文件的完整性，CRC32可以作为一种选择，但如果需要进行文件内容的精确比较和确定文件是否相同，还是建议使用更强大的哈希算法如SHA-256。
//
//
//
//
//User
//SHA-256 和我提供的计算hash的方式哪个快一点 有什么区别
//ChatGPT
//SHA-256 和你提供的计算 hash 的方式之间有一些区别，主要是在安全性和性能方面：
//
//安全性： SHA-256 是一个密码学安全的哈希算法，广泛用于数据完整性验证和数据安全，因为它提供了高度的安全性，保证不同数据会生成不同的哈希值。这是其主要优点，但也导致其计算复杂度较高。与之相比，你提供的方法更灵活，可以选择不同的哈希算法，但它们中的一些（如MD5和SHA-1）已经被认为不安全，容易受到碰撞攻击。
//
//性能： SHA-256 通常比你提供的方法的性能略低。SHA-256 是一个强大的密码学哈希算法，计算比较耗时，特别是对于大文件而言。你提供的方法采用了更简单的方式，可能会更快，但牺牲了一些安全性。
//
//总的来说，如果你需要强大的哈希算法来确保文件的唯一性和安全性，SHA-256 是更好的选择。但如果你只需要快速计算哈希值，可以根据实际需求选择其他算法。在实际应用中，你可以根据性能和安全性的平衡来选择适当的哈希算法。如果性能是你的主要关注点，并且不需要高度安全性，你可以继续使用你提供的方法。否则，建议使用 SHA-256 或其他安全哈希算法。