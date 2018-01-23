package nz.co.hexgraph.hexvalue;

import nz.co.hexgraph.producer.HexGraphProducer;

import java.util.Properties;

public class HexValueProducer extends HexGraphProducer {
    public HexValueProducer(Properties properties) {
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
