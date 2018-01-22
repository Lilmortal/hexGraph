package nz.co.hexgraph.image;

import java.time.LocalDateTime;

public class ImageMessage {
    private String imagePath;

    private LocalDateTime creationDate;

    private String hexValue;

    public ImageMessage(String imagePath, LocalDateTime creationDate, String hexValue) {
        this.imagePath = imagePath;
        this.creationDate = creationDate;
        this.hexValue = hexValue;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public String toString() {
        return "ImageMessage{" +
                "imagePath='" + imagePath + '\'' +
                ", creationDate=" + creationDate +
                ", hexValue='" + hexValue + '\'' +
                '}';
    }
}
