package nz.co.hexgraph.producers;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Map;
import java.util.Properties;

public class ProducerPropertiesBuilder {
    private Properties properties = new Properties();

    public ProducerPropertiesBuilder(String bootstrapServersConfig, String serializerClassConfig, String valueSerializerClassConfig) {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClassConfig);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClassConfig);
    }

    public ProducerPropertiesBuilder setClientIdConfig(String clientIdConfig) {
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientIdConfig);
        return this;
    }

    public ProducerPropertiesBuilder setPartitionerClassConfig(String partitionerClassConfig) {
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerClassConfig);
        return this;
    }

    public ProducerPropertiesBuilder setPartitions(Map<String, String> partitions) {
        partitions.forEach((key, value) -> properties.put(key, value));
        return this;
    }

    public Properties build() {
        return properties;
    }
}
