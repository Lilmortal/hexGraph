package nz.co.hexgraph.hexvalue;

import nz.co.hexgraph.producer.HexGraphProducerConfig;
import nz.co.hexgraph.producer.ProducerPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HexProducerFactory {
    public List<nz.co.hexgraph.producer.HexGraphProducer> build(List<HexGraphProducerConfig> hexGraphProducerConfigs) {
        List<nz.co.hexgraph.producer.HexGraphProducer> hexGraphProducers = new ArrayList<>();
        for (HexGraphProducerConfig hexGraphProducerConfig : hexGraphProducerConfigs) {
            Properties producerProperties = buildProducerProperties(hexGraphProducerConfig);
            nz.co.hexgraph.producer.HexGraphProducer hexGraphProducer = new HexValueProducer(producerProperties);

            hexGraphProducers.add(hexGraphProducer);
        }
        return hexGraphProducers;
    }

    private Properties buildProducerProperties(HexGraphProducerConfig hexGraphProducerConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(hexGraphProducerConfig.getBootstrapServerConfig(),
                hexGraphProducerConfig.getSerializerClassConfig(),
                hexGraphProducerConfig.getValueSerializerClassConfig());

        return producerPropertiesBuilder.build();
    }
}
