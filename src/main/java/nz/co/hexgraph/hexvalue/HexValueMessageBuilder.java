package nz.co.hexgraph.hexvalue;

import java.time.LocalDateTime;

public class HexValueMessageBuilder {
    private String imagePath;

    private LocalDateTime creationDate;

    private String hexValue;

    public HexValueMessageBuilder withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public HexValueMessageBuilder withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public HexValueMessageBuilder withHexValue(String hexValue) {
        this.hexValue = hexValue;
        return this;
    }

    public HexValueMessage build() {
        return new HexValueMessage(imagePath, creationDate, hexValue);
    }
}
