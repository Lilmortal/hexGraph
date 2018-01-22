package nz.co.hexgraph.image;

import nz.co.hexgraph.consumers.HexGraphConsumer;

import java.util.Properties;

public class ImageHexGraphConsumer extends HexGraphConsumer {
    public ImageHexGraphConsumer(Properties properties) {
        super(properties);
    }

    @Override
    public String name() {
        return "image consumer";
    }

    @Override
    public String version() {
        return "1";
    }
}
