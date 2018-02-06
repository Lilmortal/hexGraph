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
    public BufferedImage getImage(String imagePath) throws IOException {
        File file = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(file);

        return bufferedImage;
    }

    @Override
    public String getCreationDate(String imagePath) throws IOException {
        File file = new File(imagePath);
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//        return attr.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return attr.creationTime().toString();
    }
}
