
package com.arc.demo.test7.image;

import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageMetadataExample {
    public static void main(String[] args) {
        try {
            // 指定要解析的图像文件路径
            File file = new File("path/to/your/image.jpg");

            // 使用ImageIO读取图像文件
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(iis);

                // 获取图像的元数据
                IIOMetadata metadata = reader.getImageMetadata(0);

                // 从元数据中获取有关图像的信息
                String formatName = metadata.getNativeMetadataFormatName();
                System.out.println("Format: " + formatName);

                // 检索更多图像元数据
                String[] metadataNames = metadata.getMetadataFormatNames();
                for (String name : metadataNames) {
                    System.out.println("Metadata format: " + name);

                    // 获取特定格式的元数据
                    Node node = metadata.getAsTree(name);
                    // 在这里可以进一步处理node，例如遍历它以查找特定的信息

                    // 这里只是示例，具体操作取决于您要处理的元数据

                }
            }

            iis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
