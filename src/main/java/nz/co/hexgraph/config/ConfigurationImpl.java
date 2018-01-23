package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producers.HexGraphProducerConfig;

import java.util.List;

public class ConfigurationImpl implements Configuration {
    ConfigurationSingleton configurationSingleton = ConfigurationSingleton.getInstance();

    @Override
    public String getTopicImage() {
        return configurationSingleton.getTopicImage();
    }

    @Override
    public String getTopicHex() {
        return configurationSingleton.getTopicHex();
    }

    @Override
    public FileType getImageFileType() {
        return configurationSingleton.getImageFileType();
    }

    @Override
    public List<HexGraphProducerConfig> getHexGraphProducerConfigs() {
        return configurationSingleton.getHexGraphProducerConfigs();
    }

    @Override
    public List<HexGraphConsumerConfig> getHexGraphConsumerConfigs() {
        return configurationSingleton.getHexGraphConsumerConfigs();
    }

    @Override
    public int getImageConsumerPollTimeout() {
        return configurationSingleton.getImageConsumerPollTimeout();
    }
}
