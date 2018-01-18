package nz.co.hexgraph.image;

import nz.co.hexgraph.consumers.Consumer;

import java.util.Properties;

public class ImageConsumer extends Consumer {
    public ImageConsumer(Properties properties) {
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
