package nz.co.hexgraph.image;

import nz.co.hexgraph.consumers.ConsumerConfig;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ImageConsumerBuilder {
    public List<Consumer> build(List<ConsumerConfig> consumerConfigs) {
        List<Consumer> consumers = new ArrayList<>();
        for (ConsumerConfig consumerConfig : consumerConfigs) {
            Properties consumerProperties = buildConsumerProperties(consumerConfig);
            Consumer cameraConsumer = new ImageConsumer(consumerProperties);
            consumers.add(cameraConsumer);
        }
        return consumers;
    }

    private Properties buildConsumerProperties(ConsumerConfig consumerConfig) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(consumerConfig.getBootstrapServerConfig(),
                consumerConfig.getDeserializerClassConfig(), consumerConfig.getValueDeserializerClassConfig(),
                consumerConfig.getGroupIdConfig()).withAutoOffsetResetConfig(Optional.of(consumerConfig.getAutoOffsetResetConfig()));
        return consumerPropertiesBuilder.build();
    }
}
