package nz.co.hexgraph.hexvalue;

import java.time.LocalDateTime;

public class HexValueMessage {
    private String imagePath;

    private LocalDateTime creationDate;

    private String hexValue;

    public HexValueMessage(String imagePath, LocalDateTime creationDate, String hexValue) {
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
        return "HexValueMessage{" +
                "imagePath='" + imagePath + '\'' +
                ", creationDate=" + creationDate +
                ", hexValue='" + hexValue + '\'' +
                '}';
    }
}
