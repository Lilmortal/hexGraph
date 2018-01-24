package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BalancingRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.hexvalue.HexValueActor;
import nz.co.hexgraph.hexvalue.HexValueMessage;
import nz.co.hexgraph.hexvalue.HexValueMessageBuilder;
import nz.co.hexgraph.hexvalue.HexValueProducer;
import nz.co.hexgraph.producer.HexGraphProducer;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.ReaderFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static nz.co.hexgraph.hexvalue.HexValueActor.Message.SEND_HEX_TOPIC_MESSAGE;
import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class ImageActor extends AbstractActor {
    public static Logger LOGGER = LoggerFactory.getLogger(ImageActor.class);

    private String imagePath;

    private Configuration configuration;

    private HexValueProducer hexValueProducer;

    Router router;

    {
        int processors = Runtime.getRuntime().availableProcessors();
        List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < processors; i++) {
            ActorRef hexValueActor = getContext().actorOf(HexValueActor.props(), "imageActor" + i);
            getContext().watch(hexValueActor);
            routees.add(new ActorRefRoutee(hexValueActor));
        }
        router = new Router(new BalancingRoutingLogic(), routees);
    }

    public static Props props() {
        return Props.create(ImageActor.class);
    }

    public enum MESSAGE {
        UPDATE_PIXEL_COUNTS
    }

    public static class UpdateImagePath {
        private String imagePath;

        public UpdateImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    public static class UpdateConfiguration {
        private Configuration configuration;

        public UpdateConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }
    }

    public static class UpdateHexValueProducer {
        private HexValueProducer hexValueProducer;

        public UpdateHexValueProducer(HexValueProducer hexValueProducer) {
            this.hexValueProducer = hexValueProducer;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateImagePath.class, r -> this.imagePath = r.imagePath)
                .match(UpdateConfiguration.class, r -> this.configuration = r.configuration)
                .match(UpdateHexValueProducer.class, r -> this.hexValueProducer = r.hexValueProducer)
                .matchEquals(UPDATE_PIXEL_COUNTS, r -> {
                    FileType imageFileType = configuration.getImageFileType();
                    Reader reader = ReaderFactory.create(imageFileType);

                    BufferedImage image = reader.getImage(imagePath);
                    LocalDateTime imageCreationDate = reader.getCreationDate(imagePath);

                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();

                    // TODO: Test performance compare to just one thread handling all the pixels in an image
                    for (int y = 0; y < imageHeight; y++) {
                        for (int x = 0; x < imageWidth; x++) {
                            int rgb = 0;
                            try {
                                rgb = image.getRGB(x, y);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                LOGGER.error(e.getMessage());
                                LOGGER.error("Array out of bounds with x:" + x + ", y:" + y);
                            }

                            HexValueMessageBuilder hexValueMessageBuilderBuilder = new HexValueMessageBuilder()
                                    .withImagePath(imagePath).withCreationDate(imageCreationDate);
                            router.route(new HexValueActor.UpdateConfiguration(configuration), getSender());
                            router.route(new HexValueActor.UpdateHexValueProducer(hexValueProducer), getSender());
                            router.route(new HexValueActor.UpdateHexValueMessageBuilder(hexValueMessageBuilderBuilder), getSender());
                            router.route(new HexValueActor.UpdateRgb(rgb), getSender());
                            router.route(SEND_HEX_TOPIC_MESSAGE, getSender());
                        }
                    }
                }).build();
    }
}
