package nz.co.hexgraph.config;

public class CameraConfigProducer {
    private String bootstrapServerConfig;

    private String serializerClassConfig;

    private String valueSerializerClassConfig;

    public CameraConfigProducer(String bootstrapServerConfig, String serializerClassConfig, String valueSerializerClassConfig) {
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
}
