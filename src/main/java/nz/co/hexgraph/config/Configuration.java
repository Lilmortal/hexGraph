package nz.co.hexgraph.config;

import nz.co.hexgraph.HexGraphInitialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A POJO reading from config files.
 * At the moment it is currently reading properties file; working with yml is much more expressive but I have to import
 * a library called "snakeyml" and jackson... maybe do this later when I have time.
 */
public class Configuration {
    public static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static final String CONFIG_NAME = "config.properties";

    private String topic;

    private List<CameraConfigProducer> cameraConfigProducers = new ArrayList<>();

    private List<CameraConfigConsumer> cameraConfigConsumers = new ArrayList<>();

    private Map<String, String> partitions = new HashMap<>();

    private Configuration() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println(e);
        }

        topic = properties.getProperty("topic");

        int i = 0;

        while(properties.getProperty(String.format("camera.producer.%s.bootstrapServerConfig", i)) != null) {
            String cameraProducerBootstrapServerConfig = properties.getProperty(String.format("camera.producer.%s.bootstrapServerConfig", i));
            String cameraProducerSerializerClassConfig = properties.getProperty(String.format("camera.producer.%s.serializerClassConfig", i));
            String cameraProducerValueSerializerClassConfig = properties.getProperty(String.format("camera.producer.%s.valueSerializerClassConfig", i));
            CameraConfigProducer cameraConfigProducer = new CameraConfigProducer(cameraProducerBootstrapServerConfig, cameraProducerSerializerClassConfig,
                    cameraProducerValueSerializerClassConfig);
            cameraConfigProducers.add(cameraConfigProducer);
            i++;
        }

        i = 0;
        while(properties.getProperty(String.format("camera.consumer.%s.bootstrapServerConfig", i)) != null) {
            String cameraConsumerBootstrapServerConfig = properties.getProperty(String.format("camera.consumer.%s.bootstrapServerConfig", i));
            String cameraConsumerDeserializerClassConfig = properties.getProperty(String.format("camera.consumer.%s.deserializerClassConfig", i));
            String cameraConsumerValueDeserializerClassConfig = properties.getProperty(String.format("camera.consumer.%s.valueDeserializerClassConfig", i));
            String cameraConsumerGroupIdConfig = properties.getProperty(String.format("camera.consumer.%s.groupIdConfig", i));
            CameraConfigConsumer cameraConfigConsumer = new CameraConfigConsumer(cameraConsumerBootstrapServerConfig, cameraConsumerDeserializerClassConfig,
                    cameraConsumerValueDeserializerClassConfig, cameraConsumerGroupIdConfig);
            cameraConfigConsumers.add(cameraConfigConsumer);
            i++;
        }

        String partition1 = properties.getProperty("camera.partition.partition1");
        partitions.put("partition.1", partition1);

        String partition2 = properties.getProperty("camera.partition.partition2");
        partitions.put("partition.2", partition2);

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

    public List<CameraConfigProducer> getCameraConfigProducers() {
        return cameraConfigProducers;
    }

    public List<CameraConfigConsumer> getCameraConfigConsumers() {
        return cameraConfigConsumers;
    }

    public Map<String, String> getPartitions() {
        return partitions;
    }
}
