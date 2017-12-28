package nz.co.hexgraph.camera;

import nz.co.hexgraph.consumers.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class CameraConsumer implements Consumer {
    private KafkaConsumer<String, String> consumer;

    public CameraConsumer(Properties properties) {
        consumer = new KafkaConsumer(properties);
    }


    @Override
    public void subscribe(String topicName) {
        consumer.subscribe(Arrays.asList(topicName));
    }

    @Override
    public ConsumerRecords<String, String> poll(long timeout) {
        return consumer.poll(timeout);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
