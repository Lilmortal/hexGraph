package nz.co.hexgraph.consumers;

import nz.co.hexgraph.consumers.ConsumerSchema;

public class ConsumerValue {
    private ConsumerSchema schema;

    private String payload;

    public ConsumerSchema getSchema() {
        return schema;
    }

    public String getPayload() {
        return payload;
    }
}
