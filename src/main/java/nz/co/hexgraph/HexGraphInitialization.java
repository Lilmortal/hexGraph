package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.consumers.ConsumerConfig;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImageConsumerFactory;
import nz.co.hexgraph.consumers.ConsumerValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HexGraphInitialization {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        LOGGER.info("Config properties successfully read.");

        String topicImage = configuration.getTopicImage();
        List<ConsumerConfig> imageConsumerConfigs = configuration.getImageConsumerConfigs();
        int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();

        ImageConsumerFactory imageConsumerFactory = new ImageConsumerFactory();
        List<Consumer> imageConsumers = imageConsumerFactory.build(imageConsumerConfigs);

        ActorSystem system = ActorSystem.create("hexGraph");

        int consumerId = 0;
        for (Consumer imageConsumer : imageConsumers) {
            try {
                imageConsumer.subscribe(topicImage);

                LOGGER.info(imageConsumer.name() + " successfully subscribed to " + topicImage);

                ActorRef imageActor = system.actorOf(ImageActor.props(), "imageActor" + consumerId);
                consumerId++;

                while (true) {
                    ConsumerRecords<String, String> records = imageConsumer.poll(imageConsumerPollTimeout);
                    for (ConsumerRecord<String, String> record : records) {
                        ConsumerValue consumerValue = new ObjectMapper().readValue(record.value(), ConsumerValue.class);

                        LOGGER.debug("Image path: " + consumerValue.getPayload());
                        imageActor.tell(new ImageActor.UpdateImagePath(consumerValue.getPayload()), imageActor);
                    }
//                cameraConsumer.commitAsync();
                }
            } catch (JsonParseException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (JsonMappingException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                imageConsumer.close();
            }
        }
    }
}
