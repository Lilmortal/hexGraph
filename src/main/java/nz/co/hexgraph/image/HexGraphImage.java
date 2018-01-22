package nz.co.hexgraph.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class HexGraphImage {
    private String imagePath;
    private LocalDateTime creationDate;

    public HexGraphImage(String imagePath, LocalDateTime creationDate) {
        this.imagePath = imagePath;
        this.creationDate = creationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "HexGraphImage{" +
                "imagePath='" + imagePath + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
