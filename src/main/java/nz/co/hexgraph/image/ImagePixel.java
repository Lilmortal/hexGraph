package nz.co.hexgraph.image;

public class ImagePixel {
    private HexGraphImage hexGraphImage;

    private String hex;

    public ImagePixel(HexGraphImage hexGraphImage, String hex) {
        this.hexGraphImage = hexGraphImage;
        this.hex = hex;
    }

    public HexGraphImage getHexGraphImage() {
        return hexGraphImage;
    }

    public String getHex() {
        return hex;
    }

    @Override
    public String toString() {
        return "ImagePixel{" +
                "hexGraphImage=" + hexGraphImage +
                ", hex='" + hex + '\'' +
                '}';
    }
}
