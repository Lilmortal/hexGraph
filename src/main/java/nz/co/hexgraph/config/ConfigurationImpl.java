package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producer.HexGraphProducerConfig;

public class ConfigurationImpl implements Configuration {
    ConfigurationSingleton configurationSingleton = ConfigurationSingleton.getInstance();

    @Override
    public String getTopicImages() {
        return configurationSingleton.getTopicImages();
    }

    @Override
    public String getTopicHexCode() {
        return configurationSingleton.getTopicHexCode();
    }

    @Override
    public FileType getImageFileType() {
        return configurationSingleton.getImagesFileType();
    }

    @Override
    public HexGraphProducerConfig getHexCodeProducerConfig() {
        return configurationSingleton.getHexCodeProducerConfig();
    }

    @Override
    public HexGraphConsumerConfig getImagesConsumerConfig() {
        return configurationSingleton.getImagesConsumerConfig();
    }

    @Override
    public int getImageConsumerPollTimeout() {
        return configurationSingleton.getImageConsumerPollTimeout();
    }
}
