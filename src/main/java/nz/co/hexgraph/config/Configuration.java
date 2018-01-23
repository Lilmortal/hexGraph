package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producers.HexGraphProducerConfig;

import java.util.List;

public interface Configuration {
    String getTopicImage();

    String getTopicHex();

    FileType getImageFileType();

    List<HexGraphProducerConfig> getHexGraphProducerConfigs();

    List<HexGraphConsumerConfig> getHexGraphConsumerConfigs();

    int getImageConsumerPollTimeout();
}
