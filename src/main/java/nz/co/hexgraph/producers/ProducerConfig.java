package nz.co.hexgraph.producers;

public class ProducerConfig {
    private String bootstrapServerConfig;

    private String serializerClassConfig;

    private String valueSerializerClassConfig;

    public ProducerConfig(String bootstrapServerConfig, String serializerClassConfig, String valueSerializerClassConfig) {
        this.bootstrapServerConfig = bootstrapServerConfig;
        this.serializerClassConfig = serializerClassConfig;
        this.valueSerializerClassConfig = valueSerializerClassConfig;
    }

    public String getBootstrapServerConfig() {
        return bootstrapServerConfig;
    }

    public String getSerializerClassConfig() {
        return serializerClassConfig;
    }

    public String getValueSerializerClassConfig() {
        return valueSerializerClassConfig;
    }

    @Override
    public String toString() {
        return "ProducerConfig{" +
                "bootstrapServerConfig='" + bootstrapServerConfig + '\'' +
                ", serializerClassConfig='" + serializerClassConfig + '\'' +
                ", valueSerializerClassConfig='" + valueSerializerClassConfig + '\'' +
                '}';
    }
}
