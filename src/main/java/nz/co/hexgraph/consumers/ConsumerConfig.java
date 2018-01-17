package nz.co.hexgraph.consumers;

public class ConsumerConfig {
    private String bootstrapServerConfig;

    private String deserializerClassConfig;

    private String valueDeserializerClassConfig;

    private String groupIdConfig;

    private String autoOffsetResetConfig;

    public ConsumerConfig(String bootstrapServerConfig, String deserializerClassConfig,
                          String valueDeserializerClassConfig, String groupIdConfig, String autoOffsetResetConfig) {
        this.bootstrapServerConfig = bootstrapServerConfig;
        this.deserializerClassConfig = deserializerClassConfig;
        this.valueDeserializerClassConfig = valueDeserializerClassConfig;
        this.groupIdConfig = groupIdConfig;
        this.autoOffsetResetConfig = autoOffsetResetConfig;
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

    public String getAutoOffsetResetConfig() {
        return autoOffsetResetConfig;
    }
}
