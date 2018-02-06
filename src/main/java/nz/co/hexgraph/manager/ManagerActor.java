package nz.co.hexgraph.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import nz.co.hexgraph.HexGraphInitialization;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.hexcode.HexCodeProducer;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.image.ImagesConsumer;
import nz.co.hexgraph.producer.HexGraphProducerConfig;
import nz.co.hexgraph.producer.ProducerPropertiesBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class ManagerActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    Router router;

    {
        int processors = Runtime.getRuntime().availableProcessors();
        List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < processors; i++) {
            ActorRef imageActor = getContext().actorOf(ImageActor.props(), "imageActor" + i);
            getContext().watch(imageActor);
            routees.add(new ActorRefRoutee(imageActor));

        }
        router = new Router(new BalancingRoutingLogic(), routees);
    }

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
                    ImagesConsumer imagesConsumer = null;

                    HexGraphConsumerConfig imagesConsumerConfig = configuration.getImagesConsumerConfig();
                    imagesConsumer = buildImagesConsumer(imagesConsumerConfig);

                    HexGraphProducerConfig hexCodeProducerConfig = configuration.getHexCodeProducerConfig();
                    HexCodeProducer hexCodeProducer = buildHexCodeProducer(hexCodeProducerConfig);

                    String topicImages = configuration.getTopicImages();
                    imagesConsumer.subscribe(topicImages);

                    LOGGER.info(imagesConsumer.name() + " successfully subscribed to " + topicImages);

                    int imageConsumerPollTimeout = configuration.getImageConsumerPollTimeout();

                    while (true) {
                        ConsumerRecords<String, String> records = imagesConsumer.poll(imageConsumerPollTimeout);
                        for (ConsumerRecord<String, String> record : records) {
//                            ConsumerValue consumerValue = new ObjectMapper().readValue(record.value(), ConsumerValue.class);

                            String imagePath = record.value().replaceAll("^\"|\"$", "");
                            LOGGER.info("Image path: " + imagePath);

                            // TODO: Yes we send more messages meaning more delay BUT what if we want to update Configuration
                            // without affecting others? This is more easier to modify.
                            router.route(new ImageActor.UpdateImagePath(imagePath), getSender());
                            router.route(new ImageActor.UpdateConfiguration(configuration), getSender());
                            router.route(new ImageActor.UpdateHexCodeProducer(hexCodeProducer), getSender());
                            router.route(UPDATE_PIXEL_COUNTS, getSender());
                        }
//                cameraConsumer.commitAsync();
                    }
                }).build();
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

    private HexCodeProducer buildHexCodeProducer(HexGraphProducerConfig hexCodeProducerConfig) {
        Properties producerProperties = buildProducerProperties(hexCodeProducerConfig);
        HexCodeProducer hexCodeProducer = new HexCodeProducer(producerProperties);
        return hexCodeProducer;
    }

    private Properties buildProducerProperties(HexGraphProducerConfig hexCodeProducerConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(hexCodeProducerConfig.getBootstrapServerConfig(),
                hexCodeProducerConfig.getSerializerClassConfig(),
                hexCodeProducerConfig.getValueSerializerClassConfig());

        return producerPropertiesBuilder.build();
    }
}
