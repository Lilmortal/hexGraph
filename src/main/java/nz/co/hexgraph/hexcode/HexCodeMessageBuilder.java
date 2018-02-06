package nz.co.hexgraph.hexcode;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class HexCodeMessageBuilder {
    private String imagePath;

    private String creationDate;

    private String hexValue;

    public HexCodeMessageBuilder withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public HexCodeMessageBuilder withCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public HexCodeMessageBuilder withHexValue(String hexValue) {
        this.hexValue = hexValue;
        return this;
    }

    public HexCodeMessage build() {
        HexCodeMessageValue hexCodeMessageValue = new HexCodeMessageValue(creationDate, hexValue);
        return new HexCodeMessage(imagePath, hexCodeMessageValue);
    }
}
