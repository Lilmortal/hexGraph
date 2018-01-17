package nz.co.hexgraph.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class ImageInfo {
    private String imagePath;
    private BufferedImage image;
    private LocalDateTime creationDate;

    public ImageInfo(String imagePath, BufferedImage image, LocalDateTime creationDate) {
        this.imagePath = imagePath;
        this.image = image;
        this.creationDate = creationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BufferedImage getImage() {
        return image;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
