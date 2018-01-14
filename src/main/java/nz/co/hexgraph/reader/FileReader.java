package nz.co.hexgraph.reader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FileReader implements Reader {
    @Override
    public BufferedImage getImage(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage bufferedImage = ImageIO.read(file);

        return bufferedImage;
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//        byte[] imageBytes = byteArrayOutputStream.toByteArray();
    }
}
