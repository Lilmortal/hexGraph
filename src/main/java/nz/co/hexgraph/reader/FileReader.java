package nz.co.hexgraph.reader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FileReader implements Reader {
    @Override
    public BufferedImage getImage(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);

        return bufferedImage;
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//        byte[] imageBytes = byteArrayOutputStream.toByteArray();
    }

    @Override
    public LocalDateTime getCreationDate(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return attr.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
