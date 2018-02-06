package nz.co.hexgraph.hexcode;

public class HexCodeMessageValue {
    private String creationDate;

    private String hexCode;

    public HexCodeMessageValue(String creationDate, String hexCode) {
        this.creationDate = creationDate;
        this.hexCode = hexCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getHexCode() {
        return hexCode;
    }

    @Override
    public String toString() {
        return "HexCodeMessageValue{" +
                "creationDate=" + creationDate +
                ", hexCode='" + hexCode + '\'' +
                '}';
    }
}
