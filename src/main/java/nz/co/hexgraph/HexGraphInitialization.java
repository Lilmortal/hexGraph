package nz.co.hexgraph;

import nz.co.hexgraph.camera.CameraConsumer;
import nz.co.hexgraph.camera.CameraProducer;
import nz.co.hexgraph.config.CameraConfigConsumer;
import nz.co.hexgraph.config.CameraConfigProducer;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.partitioner.CameraPartitioner;
import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HexGraphInitialization {
    public static final Logger log = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        String topic = configuration.getTopic();
        List<CameraConfigProducer> cameraConfigProducers = configuration.getCameraConfigProducers();
        List<CameraConfigConsumer> cameraConfigConsumers = configuration.getCameraConfigConsumers();
        Map<String, String> partitionConfig = configuration.getPartitions();

//        // TODO: Come back in 1 year and see if lambda is more intuitive than for loops
//        cameraConfigProducers.stream().forEach(cameraConfigProducer -> {
//            Properties producerProperties = buildProducerProperties(cameraConfigProducer, partitionConfig);
//            Producer cameraProducer = new CameraProducer(producerProperties);
//
//            cameraProducer.send(topic, "test");
//            cameraProducer.send(topic, "LOLOLOL", (metadata, exception) ->
//                    log.info("TOPIC: " + metadata.topic() + " " + metadata.partition() + " " + metadata.offset()));
//        });


        cameraConfigConsumers.stream().forEach(cameraConfigConsumer -> {
            Properties consumerProperties = buildConsumerProperties(cameraConfigConsumer);
            Consumer cameraConsumer = new CameraConsumer(consumerProperties);
            try {
                while (true) {
                    cameraConsumer.subscribe(topic/*, new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        log.info(String.format("%s revoked by this consumer.", Arrays.toString(partitions.toArray())));
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        log.info(String.format("%s assigned to this consumer.", Arrays.toString(partitions.toArray())));
                    }
                }*/);

                    ConsumerRecords<String, String> records = cameraConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        log.info("Value: " + record.value());
                    }
//                cameraConsumer.commitAsync();
                }
            } finally {
                cameraConsumer.close();
            }
        });
    }

    private Properties buildProducerProperties(CameraConfigProducer cameraConfigProducer, Map<String, String> partitionConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(cameraConfigProducer.getBootstrapServerConfig(),
                cameraConfigProducer.getSerializerClassConfig(),
                cameraConfigProducer.getValueSerializerClassConfig());

        return producerPropertiesBuilder.withPartitionerClassConfig(CameraPartitioner.class.getCanonicalName()).withPartitions(partitionConfig).build();
    }

    private Properties buildConsumerProperties(CameraConfigConsumer cameraConfigConsumer) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(cameraConfigConsumer.getBootstrapServerConfig(),
                cameraConfigConsumer.getDeserializerClassConfig(), cameraConfigConsumer.getValueDeserializerClassConfig(),
                cameraConfigConsumer.getGroupIdConfig()).withAutoOffsetResetConfig(cameraConfigConsumer.getAutoOffsetResetConfig());
        return consumerPropertiesBuilder.build();
    }
}
