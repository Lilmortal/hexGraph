package nz.co.hexgraph.pixel;

import com.fasterxml.jackson.databind.JsonNode;
import nz.co.hexgraph.partitioner.CameraPartitioner;
import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerConfig;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HexProducerBuilder {
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

        return producerPropertiesBuilder.withPartitionerClassConfig(CameraPartitioner.class.getCanonicalName()).build();
    }
}
