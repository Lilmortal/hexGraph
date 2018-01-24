package nz.co.hexgraph.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.Properties;

public abstract class HexGraphProducer extends KafkaProducer {

    public HexGraphProducer(Map configs) {
        super(configs);
    }

    public HexGraphProducer(Map configs, Serializer keySerializer, Serializer valueSerializer) {
        super(configs, keySerializer, valueSerializer);
    }

    public HexGraphProducer(Properties properties) {
        super(properties);
    }

    public HexGraphProducer(Properties properties, Serializer keySerializer, Serializer valueSerializer) {
        super(properties, keySerializer, valueSerializer);
    }

    public abstract String name();

    public abstract String version();
}

