package nz.co.hexgraph.hexcode;

import nz.co.hexgraph.producer.HexGraphProducer;

import java.util.Properties;

public class HexCodeProducer extends HexGraphProducer {
    public HexCodeProducer(Properties properties) {
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
