package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.consumers.HexGraphConsumer;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.hex.HexProducerFactory;
import nz.co.hexgraph.image.HexGraphImage;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImageConsumerFactory;
import nz.co.hexgraph.consumers.ConsumerValue;
import nz.co.hexgraph.producers.HexGraphProducer;
import nz.co.hexgraph.producers.HexGraphProducerConfig;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.ReaderFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class HexGraphInitialization {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    public void start() {
        Configuration configuration = Configuration.getInstance();

        LOGGER.info("Config properties successfully read.");

        String topicImage = configuration.getTopicImage();
        List<HexGraphConsumerConfig> imageHexGraphConsumerConfigs = configuration.getImageHexGraphConsumerConfigs();
        int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();
        FileType fileType = configuration.getImageFileType();
        List<HexGraphProducerConfig> hexHexGraphProducerConfigs = configuration.getHexHexGraphProducerConfigs();
        String topicHex = configuration.getTopicHex();

        ImageConsumerFactory imageConsumerFactory = new ImageConsumerFactory();
        List<HexGraphConsumer> imageHexGraphConsumers = imageConsumerFactory.build(imageHexGraphConsumerConfigs);

        ActorSystem system = ActorSystem.create("hexGraph");

        HexProducerFactory hexProducerFactory = new HexProducerFactory();
        List<HexGraphProducer> hexHexGraphProducers = hexProducerFactory.build(hexHexGraphProducerConfigs);

        Reader reader = ReaderFactory.create(fileType);

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

                        File imageFile = new File(imagePath);
                        BufferedImage image = reader.getImage(imageFile);
                        LocalDateTime imageCreationDate = reader.getCreationDate(imageFile);
                        HexGraphImage hexGraphImage = new HexGraphImage(imagePath, imageCreationDate);

                        imageActor.tell(new ImageActor.UpdateHexGraphImage(hexGraphImage), imageActor);
                        imageActor.tell(new ImageActor.UpdateImage(image), imageActor);
                        imageActor.tell(new ImageActor.UpdateProducers(hexHexGraphProducers), imageActor);
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
