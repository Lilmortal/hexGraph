package nz.co.hexgraph.hexcode;

public class HexCodeMessage {
    private String imagePath;

    private HexCodeMessageValue hexCodeMessageValue;

    public HexCodeMessage(String imagePath, HexCodeMessageValue hexCodeMessageValue) {
        this.imagePath = imagePath;
        this.hexCodeMessageValue = hexCodeMessageValue;
    }

    public String getImagePath() {
        return imagePath;
    }

    public HexCodeMessageValue getHexCodeMessageValue() {
        return hexCodeMessageValue;
    }

    @Override
    public String toString() {
        return "HexCodeMessage{" +
                "imagePath='" + imagePath + '\'' +
                ", hexCodeMessageValue=" + hexCodeMessageValue +
                '}';
    }
}
