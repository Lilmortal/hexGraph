package nz.co.hexgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class ConfigReader {
    private static final String CONFIG = "config.properties";

    private String topic;

    private String bootstrapServers;

    public static void getInstance() {

    }

    public void get() {
        Properties properties = new Properties();
        Optional<InputStream> inputStream = Optional.of(getClass().getClassLoader().getResourceAsStream(CONFIG));

        if (!inputStream.isPresent()) {

            System.out.println("Config file could not be read.");
            return;
        }

        try {
            properties.load(inputStream.get());

            topic = properties.getProperty("topic");
            bootstrapServers = properties.getProperty("bootstrapServers");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream.isPresent()) {
                try {
                    inputStream.get().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getTopic() {
        return topic;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
