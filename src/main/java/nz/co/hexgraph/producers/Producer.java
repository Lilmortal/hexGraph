package nz.co.hexgraph.producers;

public interface Producer {
    void send(String topicName, String message);

    void close();
}
