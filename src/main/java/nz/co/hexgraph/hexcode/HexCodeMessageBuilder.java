package nz.co.hexgraph.hexcode;

public class HexCodeMessageBuilder {
    private String imagePath;

    private String creationDate;

    private String hexCode;

    public HexCodeMessageBuilder withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public HexCodeMessageBuilder withCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public HexCodeMessageBuilder withHexCode(String hexCode) {
        this.hexCode = hexCode;
        return this;
    }

    public HexCodeMessage build() {
        HexCodeMessageValue hexCodeMessageValue = new HexCodeMessageValue(creationDate, hexCode);
        return new HexCodeMessage(imagePath, hexCodeMessageValue);
    }
}
