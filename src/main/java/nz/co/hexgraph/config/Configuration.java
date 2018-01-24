package nz.co.hexgraph.config;

import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.producer.HexGraphProducerConfig;

public interface Configuration {
    String getTopicImages();

    String getTopicHexValue();

    FileType getImageFileType();

    HexGraphProducerConfig getHexValueProducerConfig();

    HexGraphConsumerConfig getImagesConsumerConfig();

    int getImageConsumerPollTimeout();
}
