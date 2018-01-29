package nz.co.hexgraph.hexcode;

import java.time.LocalDateTime;

public class HexCodeMessageBuilder {
    private String imagePath;

    private LocalDateTime creationDate;

    private String hexValue;

    public HexCodeMessageBuilder withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public HexCodeMessageBuilder withCreationDate(LocalDateTime creationDate) {
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
