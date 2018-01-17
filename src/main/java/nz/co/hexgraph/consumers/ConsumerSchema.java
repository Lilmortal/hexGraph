package nz.co.hexgraph.consumers;

public class ConsumerSchema {
    private String type;

    private boolean optional;

    public ConsumerSchema(String type, boolean optional) {
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
