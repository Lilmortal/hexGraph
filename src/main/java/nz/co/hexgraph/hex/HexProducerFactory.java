package nz.co.hexgraph.hex;

import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerConfig;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class HexProducerFactory {
    public List<Producer> build(List<ProducerConfig> producerConfigs) {
        List<Producer> producers = new ArrayList<>();
        for (ProducerConfig producerConfig : producerConfigs) {
            Properties producerProperties = buildProducerProperties(producerConfig);
            Producer hexProducer = new HexProducer(producerProperties);

            producers.add(hexProducer);
        }
        return producers;
    }

    private Properties buildProducerProperties(ProducerConfig producerConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(producerConfig.getBootstrapServerConfig(),
                producerConfig.getSerializerClassConfig(),
                producerConfig.getValueSerializerClassConfig());

        return producerPropertiesBuilder.build();
    }
}
