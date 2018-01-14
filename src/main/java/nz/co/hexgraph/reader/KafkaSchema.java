package nz.co.hexgraph.reader;

public class KafkaSchema {
    private String type;

    private boolean optional;

    public KafkaSchema(String type, boolean optional) {
        this.type = type;
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }
}
