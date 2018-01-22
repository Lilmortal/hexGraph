package nz.co.hexgraph.consumers;

public class HexGraphConsumerConfig {
    private String bootstrapServerConfig;

    private String deserializerClassConfig;

    private String valueDeserializerClassConfig;

    private String groupIdConfig;

    private String autoOffsetResetConfig;

    public HexGraphConsumerConfig(String bootstrapServerConfig, String deserializerClassConfig,
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

    @Override
    public String toString() {
        return "HexGraphConsumerConfig{" +
                "bootstrapServerConfig='" + bootstrapServerConfig + '\'' +
                ", deserializerClassConfig='" + deserializerClassConfig + '\'' +
                ", valueDeserializerClassConfig='" + valueDeserializerClassConfig + '\'' +
                ", groupIdConfig='" + groupIdConfig + '\'' +
                ", autoOffsetResetConfig='" + autoOffsetResetConfig + '\'' +
                '}';
    }
}
