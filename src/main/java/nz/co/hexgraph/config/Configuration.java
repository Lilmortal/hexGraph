package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producer.HexGraphProducerConfig;

public interface Configuration {
    String getTopicImages();

    String getTopicHexCode();

    FileType getImageFileType();

    HexGraphProducerConfig getHexCodeProducerConfig();

    HexGraphConsumerConfig getImagesConsumerConfig();

    int getImageConsumerPollTimeout();
}
