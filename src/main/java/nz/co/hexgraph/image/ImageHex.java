package nz.co.hexgraph.image;

public class ImageHex {
    private ImageInfo imageInfo;

    private String hex;

    public ImageHex(ImageInfo imageInfo, String hex) {
        this.imageInfo = imageInfo;
        this.hex = hex;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public String getHex() {
        return hex;
    }
}
