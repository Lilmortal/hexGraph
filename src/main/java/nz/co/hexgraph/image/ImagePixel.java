package nz.co.hexgraph.image;

public class HexGraphImage {
    private HexGraphImageDetails hexGraphImageDetails;

    private String hex;

    public HexGraphImage(HexGraphImageDetails hexGraphImageDetails, String hex) {
        this.hexGraphImageDetails = hexGraphImageDetails;
        this.hex = hex;
    }

    public HexGraphImageDetails getHexGraphImageDetails() {
        return hexGraphImageDetails;
    }

    public String getHex() {
        return hex;
    }
}
