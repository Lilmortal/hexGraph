package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producer.HexGraphProducerConfig;
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
public class ConfigurationSingleton {
    public static final Logger LOG = LoggerFactory.getLogger(ConfigurationSingleton.class);

    private static final String CONFIG_NAME = "config.properties";

    private static final String TOPIC_IMAGES_CONFIG_NAME = "topic.images";

    private static final String TOPIC_HEX_VALUE_CONFIG_NAME = "topic.hex.value";

    private static final String IMAGES_FILE_TYPE_CONFIG_NAME = "images.file.type";

    private static final String HEX_VALUE_PRODUCER_BOOTSTRAP_SERVER_CONFIG_NAME = "hex.value.producer.bootstrapServerConfig";

    private static final String HEX_VALUE_PRODUCER_SERIALIZER_CLASS_CONFIG_NAME = "hex.value.producer.serializerClassConfig";

    private static final String HEX_VALUE_PRODUCER_VALUE_SERIALIZER_CLASS_CONFIG_NAME = "hex.value.producer.valueSerializerClassConfig";

    private static final String IMAGES_CONSUMER_BOOTSTRAP_SERVER_CONFIG_NAME = "images.consumer.bootstrapServerConfig";

    private static final String IMAGES_CONSUMER_DESERIALIZER_CLASS_CONFIG_NAME = "images.consumer.deserializerClassConfig";

    private static final String IMAGES_CONSUMER_VALUE_DESERIALIZER_CLASS_CONFIG_NAME = "images.consumer.valueDeserializerClassConfig";

    private static final String IMAGES_CONSUMER_GROUP_ID_CONFIG_NAME = "images.consumer.groupIdConfig";

    private static final String IMAGES_CONSUMER_AUTO_OFFSET_RESET_CONFIG_NAME = "images.consumer.autoOffsetResetConfig";

    private static final String IMAGES_CONSUMER_POLL_TIMEOUT_CONFIG_NAME = "images.consumer.poll.timeout";

    private String topicImages;

    private String topicHexValue;

    private FileType imagesFileType;

    private HexGraphProducerConfig hexValueProducerConfig;

    private HexGraphConsumerConfig imagesConsumerConfig;

    private int imageConsumerPollTimeout;

    private ConfigurationSingleton() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        topicImages = properties.getProperty(TOPIC_IMAGES_CONFIG_NAME);
        topicHexValue = properties.getProperty(TOPIC_HEX_VALUE_CONFIG_NAME);
        imagesFileType = FileType.valueOf(properties.getProperty(IMAGES_FILE_TYPE_CONFIG_NAME).toUpperCase());

        String cameraProducerBootstrapServerConfig = properties.getProperty(HEX_VALUE_PRODUCER_BOOTSTRAP_SERVER_CONFIG_NAME);
        String cameraProducerSerializerClassConfig = properties.getProperty(HEX_VALUE_PRODUCER_SERIALIZER_CLASS_CONFIG_NAME);
        String cameraProducerValueSerializerClassConfig = properties.getProperty(HEX_VALUE_PRODUCER_VALUE_SERIALIZER_CLASS_CONFIG_NAME);
        hexValueProducerConfig = new HexGraphProducerConfig(cameraProducerBootstrapServerConfig, cameraProducerSerializerClassConfig,
                cameraProducerValueSerializerClassConfig);

        String cameraConsumerBootstrapServerConfig = properties.getProperty(IMAGES_CONSUMER_BOOTSTRAP_SERVER_CONFIG_NAME);
        String cameraConsumerDeserializerClassConfig = properties.getProperty(IMAGES_CONSUMER_DESERIALIZER_CLASS_CONFIG_NAME);
        String cameraConsumerValueDeserializerClassConfig = properties.getProperty(IMAGES_CONSUMER_VALUE_DESERIALIZER_CLASS_CONFIG_NAME);
        String cameraConsumerGroupIdConfig = properties.getProperty(IMAGES_CONSUMER_GROUP_ID_CONFIG_NAME);
        String cameraConsumerAutoOffsetResetConfig = properties.getProperty(IMAGES_CONSUMER_AUTO_OFFSET_RESET_CONFIG_NAME);
        imagesConsumerConfig = new HexGraphConsumerConfig(cameraConsumerBootstrapServerConfig,
                cameraConsumerDeserializerClassConfig, cameraConsumerValueDeserializerClassConfig,
                cameraConsumerGroupIdConfig, cameraConsumerAutoOffsetResetConfig);

        imageConsumerPollTimeout = Integer.parseInt(properties.getProperty(IMAGES_CONSUMER_POLL_TIMEOUT_CONFIG_NAME));
    }

    private static class SingletonHelper {
        private static final ConfigurationSingleton INSTANCE = new ConfigurationSingleton();
    }

    public static ConfigurationSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String getTopicImages() {
        return topicImages;
    }

    public String getTopicHexValue() {
        return topicHexValue;
    }

    public FileType getImagesFileType() {
        return imagesFileType;
    }

    public HexGraphProducerConfig getHexValueProducerConfig() {
        return hexValueProducerConfig;
    }

    public HexGraphConsumerConfig getImagesConsumerConfig() {
        return imagesConsumerConfig;
    }

    public int getImageConsumerPollTimeout() {
        return imageConsumerPollTimeout;
    }
}
