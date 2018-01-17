package nz.co.hexgraph.consumers;

public class ConsumerSchema {
    private String type;

    private boolean optional;

    public String getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "ConsumerSchema{" +
                "type='" + type + '\'' +
                ", optional=" + optional +
                '}';
    }
}
