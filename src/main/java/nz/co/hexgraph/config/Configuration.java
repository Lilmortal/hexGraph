package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.ConsumerConfig;
import nz.co.hexgraph.producers.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * A POJO reading from config files.
 * At the moment it is currently reading properties file; working with yml is much more expressive but I have to import
 * a library called "snakeyml" and jackson... maybe do this later when I have time.
 */
public class Configuration {
    public static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static final String CONFIG_NAME = "config.properties";

    private static final String TOPIC_IMAGE_CONFIG_NAME = "topic.image";

    private static final String TOPIC_HEX_CONFIG_NAME = "topic.hex";

    private static final String FILE_TYPE_CONFIG_NAME = "file.type";

    private static final String HEX_PRODUCER_BOOTSTRAP_SERVER_CONFIG_NAME = "hex.producer.%s.bootstrapServerConfig";

    private static final String HEX_PRODUCER_SERIALIZER_CLASS_CONFIG_NAME = "hex.producer.%s.serializerClassConfig";

    private static final String HEX_PRODUCER_VALUE_SERIALIZER_CLASS_CONFIG_NAME = "hex.producer.%s.valueSerializerClassConfig";

    private static final String IMAGE_CONSUMER_BOOTSTRAP_SERVER_CONFIG_NAME = "image.consumer.%s.bootstrapServerConfig";

    private static final String IMAGE_CONSUMER_DESERIALIZER_CLASS_CONFIG_NAME = "image.consumer.%s.deserializerClassConfig";

    private static final String IMAGE_CONSUMER_VALUE_DESERIALIZER_CLASS_CONFIG_NAME = "image.consumer.%s.valueDeserializerClassConfig";

    private static final String IMAGE_CONSUMER_GROUP_ID_CONFIG_NAME = "image.consumer.%s.groupIdConfig";

    private static final String IMAGE_CONSUMER_AUTO_OFFSET_RESET_CONFIG_NAME = "image.consumer.%s.autoOffsetResetConfig";

    private static final String IMAGE_CONSUMER_POLL_TIMEOUT_CONFIG_NAME = "image.consumer.poll.timeout";

    private String topicImage;

    private String topicHex;

    private FileType fileType;

    private List<ProducerConfig> hexProducerConfigs = new ArrayList<>();

    private List<ConsumerConfig> imageConsumerConfigs = new ArrayList<>();

    private int imageConsumerPollTimeout;

    private Configuration() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        topicImage = properties.getProperty(TOPIC_IMAGE_CONFIG_NAME);
        topicHex = properties.getProperty(TOPIC_HEX_CONFIG_NAME);
        fileType = FileType.valueOf(properties.getProperty(FILE_TYPE_CONFIG_NAME).toUpperCase());

        int i = 0;
        while (properties.getProperty(String.format(HEX_PRODUCER_BOOTSTRAP_SERVER_CONFIG_NAME, i)) != null) {
            String cameraProducerBootstrapServerConfig = properties.getProperty(String.format(HEX_PRODUCER_BOOTSTRAP_SERVER_CONFIG_NAME, i));
            String cameraProducerSerializerClassConfig = properties.getProperty(String.format(HEX_PRODUCER_SERIALIZER_CLASS_CONFIG_NAME, i));
            String cameraProducerValueSerializerClassConfig = properties.getProperty(String.format(HEX_PRODUCER_VALUE_SERIALIZER_CLASS_CONFIG_NAME, i));
            ProducerConfig hexProducerConfig = new ProducerConfig(cameraProducerBootstrapServerConfig, cameraProducerSerializerClassConfig,
                    cameraProducerValueSerializerClassConfig);
            hexProducerConfigs.add(hexProducerConfig);
            i++;
        }

        i = 0;
        while (properties.getProperty(String.format(IMAGE_CONSUMER_BOOTSTRAP_SERVER_CONFIG_NAME, i)) != null) {
            String cameraConsumerBootstrapServerConfig = properties.getProperty(String.format(IMAGE_CONSUMER_BOOTSTRAP_SERVER_CONFIG_NAME, i));
            String cameraConsumerDeserializerClassConfig = properties.getProperty(String.format(IMAGE_CONSUMER_DESERIALIZER_CLASS_CONFIG_NAME, i));
            String cameraConsumerValueDeserializerClassConfig = properties.getProperty(String.format(IMAGE_CONSUMER_VALUE_DESERIALIZER_CLASS_CONFIG_NAME, i));
            String cameraConsumerGroupIdConfig = properties.getProperty(String.format(IMAGE_CONSUMER_GROUP_ID_CONFIG_NAME, i));
            String cameraConsumerAutoOffsetResetConfig = properties.getProperty(String.format(IMAGE_CONSUMER_AUTO_OFFSET_RESET_CONFIG_NAME, i));
            ConsumerConfig imageConsumerConfig = new ConsumerConfig(cameraConsumerBootstrapServerConfig,
                    cameraConsumerDeserializerClassConfig, cameraConsumerValueDeserializerClassConfig,
                    cameraConsumerGroupIdConfig, cameraConsumerAutoOffsetResetConfig);
            imageConsumerConfigs.add(imageConsumerConfig);
            i++;
        }

        imageConsumerPollTimeout = Integer.parseInt(properties.getProperty(IMAGE_CONSUMER_POLL_TIMEOUT_CONFIG_NAME));
    }

    private static class SingletonHelper {
        private static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public String getTopicHex() {
        return topicHex;
    }

    public FileType getFileType() {
        return fileType;
    }

    public List<ProducerConfig> getHexProducerConfigs() {
        return hexProducerConfigs;
    }

    public List<ConsumerConfig> getImageConsumerConfigs() {
        return imageConsumerConfigs;
    }

    public int getImageConsumerPollTimeout() {
        return imageConsumerPollTimeout;
    }
}
