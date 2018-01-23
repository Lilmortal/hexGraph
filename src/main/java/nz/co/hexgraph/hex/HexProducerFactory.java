package nz.co.hexgraph.hex;

import nz.co.hexgraph.producers.HexGraphProducerConfig;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HexProducerFactory {
    public List<nz.co.hexgraph.producers.HexGraphProducer> build(List<HexGraphProducerConfig> hexGraphProducerConfigs) {
        List<nz.co.hexgraph.producers.HexGraphProducer> hexGraphProducers = new ArrayList<>();
        for (HexGraphProducerConfig hexGraphProducerConfig : hexGraphProducerConfigs) {
            Properties producerProperties = buildProducerProperties(hexGraphProducerConfig);
            nz.co.hexgraph.producers.HexGraphProducer hexGraphProducer = new HexGraphProducer(producerProperties);

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
