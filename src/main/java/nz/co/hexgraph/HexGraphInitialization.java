package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.gson.Gson;
import nz.co.hexgraph.camera.CameraConsumer;
import nz.co.hexgraph.config.CameraConfigConsumer;
import nz.co.hexgraph.config.CameraConfigProducer;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.reader.KafkaValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HexGraphInitialization {
    public static final Logger log = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        String topic = configuration.getTopicImage();
        List<CameraConfigConsumer> cameraConfigConsumers = configuration.getCameraConfigConsumers();

        ActorSystem system = ActorSystem.create("hexGraph");

        int consumerId = 0;
        for (CameraConfigConsumer cameraConfigConsumer : cameraConfigConsumers) {
            Properties consumerProperties = buildConsumerProperties(cameraConfigConsumer);
            Consumer cameraConsumer = new CameraConsumer(consumerProperties);

            try {
                ActorRef imageActor = system.actorOf(ImageActor.props(), "imageActor" + consumerId);
                consumerId++;
                while (true) {
                    cameraConsumer.subscribe(topic);

                    ConsumerRecords<String, String> records = cameraConsumer.poll(2000);
                    for (ConsumerRecord<String, String> record : records) {

                        Gson gson = new Gson();
                        KafkaValue kafkaValue = gson.fromJson(record.value(), KafkaValue.class);

                        imageActor.tell(new ImageActor.UpdateImagePath(kafkaValue.getPayload()), imageActor);
                    }
//                cameraConsumer.commitAsync();
                }
            } finally {
                cameraConsumer.close();
            }
        }
    }

    private Properties buildConsumerProperties(CameraConfigConsumer cameraConfigConsumer) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(cameraConfigConsumer.getBootstrapServerConfig(),
                cameraConfigConsumer.getDeserializerClassConfig(), cameraConfigConsumer.getValueDeserializerClassConfig(),
                cameraConfigConsumer.getGroupIdConfig()).withAutoOffsetResetConfig(cameraConfigConsumer.getAutoOffsetResetConfig());
        return consumerPropertiesBuilder.build();
    }
}
