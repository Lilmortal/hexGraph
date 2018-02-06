package nz.co.hexgraph.reader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Reader {
    BufferedImage getImage(String imagePath) throws IOException;

    String getCreationDate(String imagePath) throws IOException;
}
