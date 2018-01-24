package nz.co.hexgraph.producer;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Optional;
import java.util.Properties;

public class ProducerPropertiesBuilder {
    private static final String DEFAULT_CLIENT_ID_CONFIG = "";

    private static final String DEFAULT_PARTITIONER_CLASS_CONFIG = "org.apache.kafka.clients.producer.internals.DefaultPartitioner";

    private Properties properties = new Properties();

    public ProducerPropertiesBuilder(String bootstrapServersConfig, String serializerClassConfig, String valueSerializerClassConfig) {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClassConfig);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClassConfig);
    }

    public ProducerPropertiesBuilder withClientIdConfig(Optional<String> clientIdConfig) {
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientIdConfig.orElse(DEFAULT_CLIENT_ID_CONFIG));
        return this;
    }

    public ProducerPropertiesBuilder withPartitionerClassConfig(Optional<String> partitionerClassConfig) {
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerClassConfig.orElse(DEFAULT_PARTITIONER_CLASS_CONFIG));
        return this;
    }

    public Properties build() {
        return properties;
    }
}
