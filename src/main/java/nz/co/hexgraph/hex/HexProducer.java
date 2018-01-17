package nz.co.hexgraph.hex;

import nz.co.hexgraph.producers.Producer;

import java.util.Properties;

public class HexProducer extends Producer {
    public HexProducer(Properties properties) {
        super(properties);
    }

    @Override
    public String version() {
        return "1";
    }
}
