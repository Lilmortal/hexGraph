package nz.co.hexgraph.config;

public class CameraConfigConsumer {
    private String bootstrapServerConfig;

    private String deserializerClassConfig;

    private String valueDeserializerClassConfig;

    private String groupIdConfig;

    public CameraConfigConsumer(String bootstrapServerConfig, String deserializerClassConfig, String valueDeserializerClassConfig, String groupIdConfig) {
        this.bootstrapServerConfig = bootstrapServerConfig;
        this.deserializerClassConfig = deserializerClassConfig;
        this.valueDeserializerClassConfig = valueDeserializerClassConfig;
        this.groupIdConfig = groupIdConfig;
    }

    public String getBootstrapServerConfig() {
        return bootstrapServerConfig;
    }

    public String getDeserializerClassConfig() {
        return deserializerClassConfig;
    }

    public String getValueDeserializerClassConfig() {
        return valueDeserializerClassConfig;
    }

    public String getGroupIdConfig() {
        return groupIdConfig;
    }
}
