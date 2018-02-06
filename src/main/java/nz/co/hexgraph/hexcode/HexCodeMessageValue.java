package nz.co.hexgraph.hexcode;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class HexCodeMessageValue {
    private String creationDate;

    private String hexValue;

    public HexCodeMessageValue(String creationDate, String hexValue) {
        this.creationDate = creationDate;
        this.hexValue = hexValue;
    }

    public String getCreationDate() {
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
