package nz.co.hexgraph.hex;

import nz.co.hexgraph.producers.HexGraphProducer;

import java.util.Properties;

public class HexHexGraphProducer extends HexGraphProducer {
    public HexHexGraphProducer(Properties properties) {
        super(properties);
    }

    @Override
    public String name() {
        return "hex producer";
    }

    @Override
    public String version() {
        return "1";
    }
}
