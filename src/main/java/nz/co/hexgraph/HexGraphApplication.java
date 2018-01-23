package nz.co.hexgraph;

import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.config.ConfigurationImpl;

public class HexGraphApplication {
    public static void main(String[] args) {
        Configuration configuration = new ConfigurationImpl();

        HexGraphInitialization hexGraphInitialization = new HexGraphInitialization(configuration);
        hexGraphInitialization.start();
    }
}
