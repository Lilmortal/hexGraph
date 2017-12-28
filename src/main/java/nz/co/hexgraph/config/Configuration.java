package nz.co.hexgraph.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A POJO reading from config files.
 * At the moment it is currently reading properties file; working with yml is much more expressive but I have to import
 * a library called "snakeyml" and jackson... maybe do this later when I have time.
 */
public class Configuration {
    private static final String CONFIG_NAME = "config.properties";

    private String topic;

    private CameraConfigProducer cameraConfigProducer;

    private CameraConfigConsumer cameraConfigConsumer;

    private Configuration() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println(e);
        }

        this.topic = properties.getProperty("topic");

        String cameraProducerBootstrapServerConfig = properties.getProperty("camera.producer.bootstrapServerConfig");
        String cameraProducerSerializerClassConfig = properties.getProperty("camera.producer.serializerClassConfig");
        String cameraProducerValueSerializerClassConfig = properties.getProperty("camera.producer.valueSerializerClassConfig");
        this.cameraConfigProducer = new CameraConfigProducer(cameraProducerBootstrapServerConfig, cameraProducerSerializerClassConfig,
                cameraProducerValueSerializerClassConfig);

        String cameraConsumerBootstrapServerConfig = properties.getProperty("camera.consumer.bootstrapServerConfig");
        String cameraConsumerDeserializerClassConfig = properties.getProperty("camera.consumer.deserializerClassConfig");
        String cameraConsumerValueDeserializerClassConfig = properties.getProperty("camera.consumer.valueDeserializerClassConfig");
        String cameraConsumerGroupIdConfig = properties.getProperty("camera.consumer.groupIdConfig");
        this.cameraConfigConsumer = new CameraConfigConsumer(cameraConsumerBootstrapServerConfig, cameraConsumerDeserializerClassConfig,
                cameraConsumerValueDeserializerClassConfig, cameraConsumerGroupIdConfig);
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

    public CameraConfigProducer getCameraConfigProducer() {
        return cameraConfigProducer;
    }

    public CameraConfigConsumer getCameraConfigConsumer() {
        return cameraConfigConsumer;
    }
}
