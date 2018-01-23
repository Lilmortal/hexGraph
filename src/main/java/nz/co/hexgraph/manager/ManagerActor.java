package nz.co.hexgraph.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.HexGraphInitialization;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.consumers.ConsumerValue;
import nz.co.hexgraph.consumers.HexGraphConsumer;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.hexvalue.HexValueProducer;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImagesConsumer;
import nz.co.hexgraph.producer.HexGraphProducerConfig;
import nz.co.hexgraph.producer.ProducerPropertiesBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class ManagerActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    private Configuration configuration;

    public enum MESSAGE {
        START
    }

    public static Props props() {
        return Props.create(ManagerActor.class);
    }

    public static class UpdateConfiguration {
        private Configuration configuration;

        public UpdateConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateConfiguration.class, r -> this.configuration = r.configuration)
                .matchEquals(MESSAGE.START, r -> {
                    HexGraphConsumerConfig imagesConsumerConfig = configuration.getImagesConsumerConfig();
                    ImagesConsumer imagesConsumer = buildImagesConsumer(imagesConsumerConfig);

                    HexGraphProducerConfig hexValueProducerConfig = configuration.getHexValueProducerConfig();
                    HexValueProducer hexValueProducer = buildHexValueProducer(hexValueProducerConfig);

                    String topicImages = configuration.getTopicImages();
                    imagesConsumer.subscribe(topicImages);

                    LOGGER.info(imagesConsumer.name() + " successfully subscribed to " + topicImages);

                    ActorRef imageActor = getContext().actorOf(ImageActor.props(), "imageActor");

                    int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();

                    while (true) {
                        ConsumerRecords<String, String> records = imagesConsumer.poll(imageConsumerPollTimeout);
                        for (ConsumerRecord<String, String> record : records) {
                            ConsumerValue consumerValue = new ObjectMapper().readValue(record.value(), ConsumerValue.class);

                            String imagePath = consumerValue.getPayload();
                            LOGGER.debug("Image path: " + imagePath);

                            // TODO: Create a POJO with this stuff
                            imageActor.tell(new ImageActor.UpdateImagePath(imagePath), imageActor);
                            imageActor.tell(new ImageActor.UpdateFileType(fileType), imageActor);
                            imageActor.tell(new ImageActor.UpdateProducers(hexValueProducer), imageActor);
                            imageActor.tell(new ImageActor.UpdateTopic(topicHexValue), imageActor);
                            imageActor.tell(UPDATE_PIXEL_COUNTS, imageActor);
                        }
//                cameraConsumer.commitAsync();
                    }
                }
    })
            .

    build();

}

    private ImagesConsumer buildImagesConsumer(HexGraphConsumerConfig imagesConsumerConfig) {
        Properties consumerProperties = buildConsumerProperties(imagesConsumerConfig);
        ImagesConsumer imagesConsumer = new ImagesConsumer(consumerProperties);
        return imagesConsumer;
    }

    private Properties buildConsumerProperties(HexGraphConsumerConfig imagesConsumerConfig) {
        ConsumerPropertiesBuilder consumerPropertiesBuilder = new ConsumerPropertiesBuilder(imagesConsumerConfig.getBootstrapServerConfig(),
                imagesConsumerConfig.getDeserializerClassConfig(), imagesConsumerConfig.getValueDeserializerClassConfig(),
                imagesConsumerConfig.getGroupIdConfig()).withAutoOffsetResetConfig(Optional.of(imagesConsumerConfig.getAutoOffsetResetConfig()));
        return consumerPropertiesBuilder.build();
    }

    private HexValueProducer buildHexValueProducer(HexGraphProducerConfig hexValueProducerConfig) {
        Properties producerProperties = buildProducerProperties(hexValueProducerConfig);
        HexValueProducer hexValueProducer = new HexValueProducer(producerProperties);
        return hexValueProducer;
    }

    private Properties buildProducerProperties(HexGraphProducerConfig hexValueProducerConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(hexValueProducerConfig.getBootstrapServerConfig(),
                hexValueProducerConfig.getSerializerClassConfig(),
                hexValueProducerConfig.getValueSerializerClassConfig());

        return producerPropertiesBuilder.build();
    }
}
