package nz.co.hexgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_NAME = "config.properties";

    private final String topic;

    private final String bootstrapServers;

    private Configuration() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println(e);
        }

        this.topic = properties.getProperty("topic");
        this.bootstrapServers = properties.getProperty("bootstrapServers");
    }

    private static class SingletonHelper {
        private static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String getTopic() {
        return topic;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
