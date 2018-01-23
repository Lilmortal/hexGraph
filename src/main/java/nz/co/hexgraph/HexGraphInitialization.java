package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.config.ConfigurationImpl;
import nz.co.hexgraph.config.ConfigurationSingleton;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.consumers.HexGraphConsumer;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.hex.HexProducerFactory;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImageConsumerFactory;
import nz.co.hexgraph.consumers.ConsumerValue;
import nz.co.hexgraph.producers.HexGraphProducer;
import nz.co.hexgraph.producers.HexGraphProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class HexGraphInitialization {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    private String topicImage;

    private String topicHex;

    private FileType fileType;

    private int imageConsumerPollTimeout;

    private List<HexGraphConsumerConfig> imageHexGraphConsumerConfigs;

    private List<HexGraphProducerConfig> hexGraphProducerConfigs;

    public HexGraphInitialization(Configuration configuration) {
        topicImage = configuration.getTopicImage();
        topicHex = configuration.getTopicHex();
        fileType = configuration.getImageFileType();
        imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();
        imageHexGraphConsumerConfigs = configuration.getHexGraphConsumerConfigs();
        hexGraphProducerConfigs = configuration.getHexGraphProducerConfigs();
    }

    public void start() {
        ImageConsumerFactory imageConsumerFactory = new ImageConsumerFactory();
        List<HexGraphConsumer> imageHexGraphConsumers = imageConsumerFactory.build(imageHexGraphConsumerConfigs);

        HexProducerFactory hexProducerFactory = new HexProducerFactory();
        List<HexGraphProducer> hexGraphProducers = hexProducerFactory.build(hexGraphProducerConfigs);

        ActorSystem system = ActorSystem.create("hexGraph");

        // TODO: Put inside ManagerActor
        int consumerId = 0;
        for (HexGraphConsumer imageHexGraphConsumer : imageHexGraphConsumers) {
            try {
                imageHexGraphConsumer.subscribe(topicImage);

                LOGGER.info(imageHexGraphConsumer.name() + " successfully subscribed to " + topicImage);

                ActorRef imageActor = system.actorOf(ImageActor.props(), "imageActor" + consumerId);
                consumerId++;

                while (true) {
                    ConsumerRecords<String, String> records = imageHexGraphConsumer.poll(imageConsumerPollTimeout);
                    for (ConsumerRecord<String, String> record : records) {
                        ConsumerValue consumerValue = new ObjectMapper().readValue(record.value(), ConsumerValue.class);

                        String imagePath = consumerValue.getPayload();
                        LOGGER.debug("Image path: " + imagePath);

                        // TODO: Create a POJO with this stuff
                        imageActor.tell(new ImageActor.UpdateImagePath(imagePath), imageActor);
                        imageActor.tell(new ImageActor.UpdateFileType(fileType), imageActor);
                        imageActor.tell(new ImageActor.UpdateProducers(hexGraphProducers), imageActor);
                        imageActor.tell(new ImageActor.UpdateTopic(topicHex), imageActor);
                        imageActor.tell(UPDATE_PIXEL_COUNTS, imageActor);
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
                imageHexGraphConsumer.close();
            }
        }
    }
}
