package nz.co.hexgraph.image;

import nz.co.hexgraph.consumers.HexGraphConsumer;

import java.util.Properties;

public class ImagesConsumer extends HexGraphConsumer {
    public ImagesConsumer(Properties properties) {
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
