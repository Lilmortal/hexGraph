package nz.co.hexgraph.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.Properties;

public abstract class Producer extends KafkaProducer {

    public Producer(Map configs) {
        super(configs);
    }

    public Producer(Map configs, Serializer keySerializer, Serializer valueSerializer) {
        super(configs, keySerializer, valueSerializer);
    }

    public Producer(Properties properties) {
        super(properties);
    }

    public Producer(Properties properties, Serializer keySerializer, Serializer valueSerializer) {
        super(properties, keySerializer, valueSerializer);
    }

    public abstract String name();

    public abstract String version();
}

