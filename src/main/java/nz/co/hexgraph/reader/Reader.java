package nz.co.hexgraph.reader;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Reader {
    BufferedImage getImage(String filePath) throws IOException;
}
