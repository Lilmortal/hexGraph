package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.consumers.ConsumerConfig;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.Consumer;
import nz.co.hexgraph.hex.HexProducerFactory;
import nz.co.hexgraph.image.HexGraphImage;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImageConsumerFactory;
import nz.co.hexgraph.consumers.ConsumerValue;
import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerConfig;
import nz.co.hexgraph.reader.FileReader;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.ReaderFactory;
import nz.co.hexgraph.reader.S3Reader;
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
        List<ConsumerConfig> imageConsumerConfigs = configuration.getImageConsumerConfigs();
        int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();
        FileType fileType = configuration.getImageFileType();
        List<ProducerConfig> hexProducerConfigs = configuration.getHexProducerConfigs();
        String topicHex = configuration.getTopicHex();

        ImageConsumerFactory imageConsumerFactory = new ImageConsumerFactory();
        List<Consumer> imageConsumers = imageConsumerFactory.build(imageConsumerConfigs);

        ActorSystem system = ActorSystem.create("hexGraph");

        HexProducerFactory hexProducerFactory = new HexProducerFactory();
        List<Producer> hexProducers = hexProducerFactory.build(hexProducerConfigs);

        Reader reader = ReaderFactory.getReader(fileType);

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

                        String imagePath = consumerValue.getPayload();
                        LOGGER.debug("Image path: " + imagePath);

                        File imageFile = new File(imagePath);
                        BufferedImage image = reader.getImage(imageFile);
                        LocalDateTime imageCreationDate = reader.getCreationDate(imageFile);
                        HexGraphImage hexGraphImage = new HexGraphImage(imagePath, imageCreationDate);

                        imageActor.tell(new ImageActor.UpdateHexGraphImage(hexGraphImage), imageActor);
                        imageActor.tell(new ImageActor.UpdateImage(image), imageActor);
                        imageActor.tell(new ImageActor.UpdateProducers(hexProducers), imageActor);
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
                imageConsumer.close();
            }
        }
    }
}
