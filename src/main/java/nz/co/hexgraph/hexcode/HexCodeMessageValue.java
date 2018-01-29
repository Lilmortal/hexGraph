package nz.co.hexgraph.hexcode;

import java.time.LocalDateTime;

public class HexCodeMessageValue {
    private LocalDateTime creationDate;

    private String hexValue;

    public HexCodeMessageValue(LocalDateTime creationDate, String hexValue) {
        this.creationDate = creationDate;
        this.hexValue = hexValue;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public String toString() {
        return "HexCodeMessageValue{" +
                "creationDate=" + creationDate +
                ", hexValue='" + hexValue + '\'' +
                '}';
    }
}
