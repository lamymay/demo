package com.arc.demo.test7.image;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

public class FileMetadataExample {
    public static void main(String[] args) {
        try {
            // 指定要解析的图像文件路径
            File file = new File("path/to/your/image.jpg");

            // 使用ImageIO读取图像文件
            BufferedImage image = ImageIO.read(file);

            // 获取图像的创建时间
            Date creationDate = new Date(file.lastModified());

            // 输出图像的元数据
            System.out.println("Image File: " + file.getName());
            System.out.println("Creation Time: " + creationDate);
            System.out.println("Image Width: " + image.getWidth());
            System.out.println("Image Height: " + image.getHeight());

            // 如果需要更多的元数据，您可以使用ImageIO的getImageMetadata方法
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(ImageIO.createImageInputStream(file));
                IIOMetadata metadata = reader.getImageMetadata(0);

                // 在这里可以进一步处理其他图像元数据

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
