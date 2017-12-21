package nz.co.hexgraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexGraphInitialization {
    public static final Logger log = LoggerFactory.getLogger(HexGraphInitialization.class);

    public static void start() {
        Configuration configuration = Configuration.getInstance();

        String topic = configuration.getTopic();
        String bootstrapServer = configuration.getBootstrapServers();

        log.info(topic + " " + bootstrapServer);
    }
}
