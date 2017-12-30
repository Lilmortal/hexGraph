package nz.co.hexgraph.camera;

import nz.co.hexgraph.producers.Producer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class CameraProducer implements Producer {
    private org.apache.kafka.clients.producer.Producer producer;

    public CameraProducer(Properties properties) {
        producer = new KafkaProducer<String, String>(properties);
    }

    @Override
    public void send(String topicName, String message) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord(topicName, message);
        producer.send(producerRecord);
    }

    @Override
    public void send(String topicName, String message, Callback callback) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord(topicName, message);
        producer.send(producerRecord, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}
