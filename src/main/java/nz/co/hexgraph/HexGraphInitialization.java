package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.gson.Gson;
import nz.co.hexgraph.camera.CameraConsumer;
import nz.co.hexgraph.config.CameraConfigConsumer;
import nz.co.hexgraph.config.CameraConfigProducer;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.partitioner.CameraPartitioner;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;
import nz.co.hexgraph.reader.FileReader;
import nz.co.hexgraph.reader.KafkaValue;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.S3Reader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
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

        ActorSystem system = ActorSystem.create("hexGraph");

        int consumerId = 0;
        for (CameraConfigConsumer cameraConfigConsumer : cameraConfigConsumers) {
            log.info("Consumer is running...");
            Properties consumerProperties = buildConsumerProperties(cameraConfigConsumer);
            Consumer cameraConsumer = new CameraConsumer(consumerProperties);
            ++consumerId;

            try {
                ActorRef imageActor = system.actorOf(ImageActor.props(), "imageActor" + consumerId);
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

                    ConsumerRecords<String, String> records = cameraConsumer.poll(2000);
                    for (ConsumerRecord<String, String> record : records) {
                        // Here we are going to start an actor which spawns multiple actors based on number of threads,
                        // and get the image from record file which is stored in S3. Each thread get no of pixels / no of threads, and
                        // have a producer pass hex value of each pixel to kafka topic (hexValue)
//                        log.info(record.value());

                        FileType fileType = configuration.getFileType();

                        Reader reader = null;
                        switch (fileType) {
                            case FILE:
                                log.info("");
                                reader = new FileReader();
                                break;
                            case S3:
                                reader = new S3Reader();
                                break;
                            default:
                                // TODO:
                        }

                        Gson gson = new Gson();
                        KafkaValue kafkaValue = gson.fromJson(record.value(), KafkaValue.class);

                        try {
                            BufferedImage image = reader.getImage(kafkaValue.getPayload());

                            int[] pixels = image.getRaster().getPixel(0, 0, new int[3]);

//                            int alpha = (pixel >> 24) & 0xff;
//                            int red = (pixel >> 16) & 0xff;
//                            int green = (pixel >> 8) & 0xff;
//                            int blue = (pixel) & 0xff;

                            log.info(pixels[0] + " " + pixels[1] + " " + pixels[2]);

                            imageActor.tell(new ImageActor.UpdateImage(image), imageActor);
                        } catch (IOException e) {
                            log.info(e.getMessage());
                        }
                    }
//                cameraConsumer.commitAsync();
                }
            } finally {
                cameraConsumer.close();
            }
        }
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
