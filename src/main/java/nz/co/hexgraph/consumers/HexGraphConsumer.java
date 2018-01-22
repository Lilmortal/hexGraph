package nz.co.hexgraph.consumers;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public abstract class HexGraphConsumer extends KafkaConsumer {

    public HexGraphConsumer(Map configs) {
        super(configs);
    }

    public HexGraphConsumer(Map configs, Deserializer keyDeserializer, Deserializer valueDeserializer) {
        super(configs, keyDeserializer, valueDeserializer);
    }

    public HexGraphConsumer(Properties properties) {
        super(properties);
    }

    public HexGraphConsumer(Properties properties, Deserializer keyDeserializer, Deserializer valueDeserializer) {
        super(properties, keyDeserializer, valueDeserializer);
    }

    public void subscribe(String topic) {
        super.subscribe(Arrays.asList(topic));
    }

    public abstract String name();

    public abstract String version();
}
