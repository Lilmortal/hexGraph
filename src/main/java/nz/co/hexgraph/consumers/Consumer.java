package nz.co.hexgraph.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface Consumer {
    void subscribe(String topicName);

    ConsumerRecords<String, String> poll(long timeout);

    void close();
}
