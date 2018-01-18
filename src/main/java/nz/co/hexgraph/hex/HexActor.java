package nz.co.hexgraph.hex;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.image.ImagePixel;
import nz.co.hexgraph.image.HexGraphImage;
import nz.co.hexgraph.producers.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;

public class HexActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexActor.class);

    private BufferedImage image;

    private HexGraphImage hexGraphImage;

    private List<Producer> producers;

    private String topic;

    private int from;

    private int to;

    public static Props props() {
        return Props.create(HexActor.class);
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
        private List<Producer> producers;

        public UpdateProducers(List<Producer> producers) {
            this.producers = producers;
        }
    }

    public static class UpdateTopic {
        private String topic;

        public UpdateTopic(String topic) {
            this.topic = topic;
        }
    }

    public static class UpdatePosition {
        private int from;
        private int to;

        public UpdatePosition(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    public enum Message {
        TOPIC_MESSAGE_SENT
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateHexGraphImage.class, r -> this.hexGraphImage = r.hexGraphImage)
                .match(UpdateImage.class, r -> this.image = r.image)
                .match(UpdateProducers.class, r -> this.producers = r.producers)
                .match(UpdateTopic.class, r -> this.topic = r.topic)
                .match(UpdatePosition.class, r -> {
                    this.from = r.from;
                    this.to = r.to;

                    int width = image.getWidth();

                    for (int i = from; i < to; i++) {
                        int x, y;

                        if (i < width) {
                            x = i;
                            y = 0;
                        } else {
                            y = i / width;
                            x = i % width;
                        }

                        int rgb = 0;
                        try {
                            rgb = image.getRGB(x, y);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LOGGER.error(e.getMessage());
                            LOGGER.error("Array out of bounds with x:" + x + ", y:" + y);
                        }

                        int red = (rgb >> 16) & 0x000000FF;
                        int green = (rgb >> 8) & 0x000000FF;
                        int blue = (rgb) & 0x000000FF;

                        String hex = String.format("#%02x%02x%02x", red, green, blue);

                        LOGGER.debug("Image with path " + hexGraphImage.getImagePath() + "has rgb value as " +
                                red + "," + green + "," + "blue" + " with it's hex code as " + hex);

                        ImagePixel imagePixel = new ImagePixel(hexGraphImage, hex);
                        byte[] imagePixelBytes = new ObjectMapper().writeValueAsBytes(imagePixel);

                        for (Producer producer : producers) {
                            ProducerRecord<String, byte[]> hexProducerRecord = new ProducerRecord<>(topic, imagePixelBytes);
                            producer.send(hexProducerRecord);
                        }


                        getSender().tell(Message.TOPIC_MESSAGE_SENT, getSelf());
                    }

                }).build();
    }
}
