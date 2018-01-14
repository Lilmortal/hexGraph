package nz.co.hexgraph.reader;

public class KafkaValue {
    private KafkaSchema kafkaSchema;

    private String payload;

    public KafkaValue(KafkaSchema kafkaSchema, String payload) {
        this.kafkaSchema = kafkaSchema;
        this.payload = payload;
    }

    public KafkaSchema getKafkaSchema() {
        return kafkaSchema;
    }

    public String getPayload() {
        return payload;
    }
}
