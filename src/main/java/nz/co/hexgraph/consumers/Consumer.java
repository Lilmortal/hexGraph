package nz.co.hexgraph.consumers;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface Consumer {
    void subscribe(String topicName);

    void subscribe(String topicName, ConsumerRebalanceListener consumerRebalanceListener);

    ConsumerRecords<String, String> poll(long timeout);

    void close();
}
