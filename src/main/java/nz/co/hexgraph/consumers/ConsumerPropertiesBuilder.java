package nz.co.hexgraph.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Optional;
import java.util.Properties;

public class ConsumerPropertiesBuilder {
    private static final String DEFAULT_AUTO_OFFSET_RESET = "largest";

    private Properties properties = new Properties();

    public ConsumerPropertiesBuilder(String bootstrapServersConfig, String deserializerClassConfig,
                                     String valueDeserializerClassConfig, String groupIdConfig) {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializerClassConfig);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClassConfig);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdConfig);
    }

    public ConsumerPropertiesBuilder withAutoOffsetResetConfig(Optional<String> autoOffsetResetConfig) {
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig.orElse(DEFAULT_AUTO_OFFSET_RESET));
        return this;
    }

    public Properties build() {
        return properties;
    }

}
