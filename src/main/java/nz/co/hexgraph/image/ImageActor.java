package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import nz.co.hexgraph.hex.HexActor;
import nz.co.hexgraph.producers.HexGraphProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;

import static nz.co.hexgraph.hex.HexActor.Message.SEND_HEX_TOPIC_MESSAGE;
import static nz.co.hexgraph.hex.HexActor.Message.TOPIC_MESSAGE_SENT;
import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class ImageActor extends AbstractActor {
    public static Logger LOGGER = LoggerFactory.getLogger(ImageActor.class);

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private HexGraphImage hexGraphImage;

    private BufferedImage image;

    private List<HexGraphProducer> hexGraphProducers;

    private String topic;

    public static Props props() {
        return Props.create(ImageActor.class);
    }

    public enum MESSAGE {
        UPDATE_PIXEL_COUNTS
    }

    public static class UpdateHexGraphImage {
        private HexGraphImage hexGraphImage;

        public UpdateHexGraphImage(HexGraphImage hexGraphImage) {
            this.hexGraphImage = hexGraphImage;
        }
    }

    public static class UpdateImage {
        private BufferedImage image;

        public UpdateImage(BufferedImage image) {
            this.image = image;
        }
    }

    public static class UpdateProducers {
        private List<HexGraphProducer> hexGraphProducers;

        public UpdateProducers(List<HexGraphProducer> hexGraphProducers) {
            this.hexGraphProducers = hexGraphProducers;
        }
    }

    public static class UpdateTopic {
        private String topic;

        public UpdateTopic(String topic) {
            this.topic = topic;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateHexGraphImage.class, r -> this.hexGraphImage = r.hexGraphImage)
                .match(UpdateImage.class, r -> this.image = r.image)
                .match(UpdateProducers.class, r -> this.hexGraphProducers = r.hexGraphProducers)
                .match(UpdateTopic.class, r -> this.topic = r.topic)
                .matchEquals(UPDATE_PIXEL_COUNTS, r -> {
                    int numberOfPixels = image.getWidth() * image.getHeight();

                    LOGGER.info("There are " +  numberOfPixels + " pixels split between " + PROCESSORS + " processors.");

                    int numberOfPixelsPerProcessor = numberOfPixels / PROCESSORS;

                    // TODO: Test performance
                    for (int i = 1; i <= PROCESSORS; i++) {
                        int pos = numberOfPixelsPerProcessor * i;
                        ActorRef hexActor = getContext().actorOf(HexActor.props());
                        hexActor.tell(new HexActor.UpdateHexGraphImage(hexGraphImage), getSelf());
                        hexActor.tell(new HexActor.UpdateImage(image), getSelf());
                        hexActor.tell(new HexActor.UpdateProducers(hexGraphProducers), getSelf());
                        hexActor.tell(new HexActor.UpdateTopic(topic), getSelf());
                        hexActor.tell(new HexActor.UpdatePosition(pos - numberOfPixelsPerProcessor, pos), getSelf());
                        hexActor.tell(SEND_HEX_TOPIC_MESSAGE, getSelf());
                    }
                }).matchEquals(TOPIC_MESSAGE_SENT, r -> LOGGER.info("Message sent to topic.")).build();
    }
}
