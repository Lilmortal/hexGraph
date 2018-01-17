package nz.co.hexgraph.consumers;

import nz.co.hexgraph.consumers.ConsumerSchema;

public class ConsumerValue {
    private ConsumerSchema consumerSchema;

    private String payload;

    public ConsumerValue(ConsumerSchema consumerSchema, String payload) {
        this.consumerSchema = consumerSchema;
        this.payload = payload;
    }

    public ConsumerSchema getConsumerSchema() {
        return consumerSchema;
    }

    public String getPayload() {
        return payload;
    }
}
