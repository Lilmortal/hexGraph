package nz.co.hexgraph.reader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class S3Reader implements Reader {

    @Override
    public BufferedImage getImage(String imagePath) throws IOException {
        return null;
    }

    @Override
    public String getCreationDate(String imagePath) throws IOException {
        return null;
    }
}
