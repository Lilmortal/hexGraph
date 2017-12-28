package nz.co.hexgraph;

import nz.co.hexgraph.camera.CameraConsumer;
import nz.co.hexgraph.camera.CameraProducer;
import nz.co.hexgraph.config.CameraConfigConsumer;
import nz.co.hexgraph.config.CameraConfigProducer;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HexGraphInitialization {
    public static final Logger log = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        String topic = configuration.getTopic();
        CameraConfigProducer cameraConfigProducer = configuration.getCameraConfigProducer();
        CameraConfigConsumer cameraConfigConsumer = configuration.getCameraConfigConsumer();

        Properties producerProperties = buildProducerProperties(cameraConfigProducer);
        Producer cameraProducer = new CameraProducer(producerProperties);

        cameraProducer.send(topic, "test");
        cameraProducer.send(topic, "LOLOLOL");

        Properties consumerProperties = buildConsumerProperties(cameraConfigConsumer);
        Consumer cameraConsumer = new CameraConsumer(consumerProperties);

        cameraConsumer.subscribe(topic);

        ConsumerRecords<String, String> records = cameraConsumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
            log.info(record.value());
        }

        cameraProducer.close();
        cameraConsumer.close();
    }

    private Properties buildProducerProperties(CameraConfigProducer cameraConfigProducer) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(cameraConfigProducer.getBootstrapServerConfig(),
                cameraConfigProducer.getSerializerClassConfig(),
                cameraConfigProducer.getValueSerializerClassConfig());
        return producerPropertiesBuilder.build();
    }

    private Properties buildConsumerProperties(CameraConfigConsumer cameraConfigConsumer) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(cameraConfigConsumer.getBootstrapServerConfig(),
                cameraConfigConsumer.getDeserializerClassConfig(), cameraConfigConsumer.getValueDeserializerClassConfig(),
                cameraConfigConsumer.getGroupIdConfig());
        return consumerPropertiesBuilder.build();
    }
}
