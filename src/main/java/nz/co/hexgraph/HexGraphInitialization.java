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
import nz.co.hexgraph.image.ImageConsumerBuilder;
import nz.co.hexgraph.consumers.ConsumerValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HexGraphInitialization {
    public static final Logger LOG = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        String topicImage = configuration.getTopicImage();
        List<ConsumerConfig> imageConsumerConfigs = configuration.getImageConsumerConfigs();
        int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();

        ImageConsumerBuilder imageConsumerBuilder = new ImageConsumerBuilder();
        List<Consumer> imageConsumers = imageConsumerBuilder.build(imageConsumerConfigs);

        ActorSystem system = ActorSystem.create("hexGraph");

        int consumerId = 0;
        for (Consumer imageConsumer : imageConsumers) {
            try {
                ActorRef imageActor = system.actorOf(ImageActor.props(), "imageActor" + consumerId);
                consumerId++;
                while (true) {
                    imageConsumer.subscribe(topicImage);

                    ConsumerRecords<String, String> records = imageConsumer.poll(imageConsumerPollTimeout);
                    for (ConsumerRecord<String, String> record : records) {
                        LOG.debug(record.value());
                        ConsumerValue consumerValue = new ObjectMapper().readValue(record.value(), ConsumerValue.class);

                        imageActor.tell(new ImageActor.UpdateImagePath(consumerValue.getPayload()), imageActor);
                    }
//                cameraConsumer.commitAsync();
                }
            } catch (JsonParseException e) {
                LOG.error(e.getMessage(), e);
            } catch (JsonMappingException e) {
                LOG.error(e.getMessage(), e);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                imageConsumer.close();
            }
        }
    }
}
