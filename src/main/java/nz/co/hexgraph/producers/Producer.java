package nz.co.hexgraph.producers;

import org.apache.kafka.clients.producer.Callback;

public interface Producer {
    void send(String topicName, String message);

    void send(String topicName, String message, Callback callback);

    void close();
}
