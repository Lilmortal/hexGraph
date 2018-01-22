package nz.co.hexgraph.image;

import nz.co.hexgraph.consumers.HexGraphConsumer;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ImageConsumerFactory {
    public List<HexGraphConsumer> build(List<HexGraphConsumerConfig> hexGraphConsumerConfigs) {
        List<HexGraphConsumer> hexGraphConsumers = new ArrayList<>();
        for (HexGraphConsumerConfig hexGraphConsumerConfig : hexGraphConsumerConfigs) {
            Properties consumerProperties = buildConsumerProperties(hexGraphConsumerConfig);
            HexGraphConsumer cameraHexGraphConsumer = new ImageHexGraphConsumer(consumerProperties);
            hexGraphConsumers.add(cameraHexGraphConsumer);
        }
        return hexGraphConsumers;
    }

    private Properties buildConsumerProperties(HexGraphConsumerConfig hexGraphConsumerConfig) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(hexGraphConsumerConfig.getBootstrapServerConfig(),
                hexGraphConsumerConfig.getDeserializerClassConfig(), hexGraphConsumerConfig.getValueDeserializerClassConfig(),
                hexGraphConsumerConfig.getGroupIdConfig()).withAutoOffsetResetConfig(Optional.of(hexGraphConsumerConfig.getAutoOffsetResetConfig()));
        return consumerPropertiesBuilder.build();
    }
}
