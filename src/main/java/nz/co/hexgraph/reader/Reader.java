package nz.co.hexgraph.reader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Reader {
    BufferedImage getImage(File file) throws IOException;

    LocalDateTime getCreationDate(File file) throws IOException;
}
