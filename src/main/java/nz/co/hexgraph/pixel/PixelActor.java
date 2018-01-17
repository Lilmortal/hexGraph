package nz.co.hexgraph.pixel;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.image.ImageHex;
import nz.co.hexgraph.image.ImageInfo;
import nz.co.hexgraph.producers.ProducerConfig;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.producers.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Properties;

public class PixelActor extends AbstractActor {
    public static final Logger LOG = LoggerFactory.getLogger(PixelActor.class);

    private BufferedImage image;

    private ImageInfo imageInfo;

    private int from;

    private int to;

    public static Props props() {
        return Props.create(PixelActor.class);
    }

    public static class UpdateImageInfo {
        private ImageInfo imageInfo;

        public UpdateImageInfo(ImageInfo imageInfo) {
            this.imageInfo = imageInfo;
        }
    }

    public static class UpdateImage {
        private BufferedImage image;

        public UpdateImage(BufferedImage image) {
            this.image = image;
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
        return receiveBuilder().match(UpdateImageInfo.class, r -> {
            this.imageInfo = r.imageInfo;
        }).match(UpdateImage.class, r -> {
            this.image = r.image;
        }).match(UpdatePosition.class, r -> {
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
                    LOG.error(e.getMessage());
                    LOG.error("Array out of bounds with x:" + x + ", y:" + y);
                }

                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;

                String hex = String.format("#%02x%02x%02x", red, green, blue);

                ImageHex imageHex = new ImageHex(imageInfo, hex);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.valueToTree(imageHex);

                Configuration configuration = Configuration.getInstance();

                List<ProducerConfig> producerConfigs = configuration.getHexProducerConfigs();
                String topicHex = configuration.getTopicHex();

                HexProducerBuilder hexProducerBuilder = new HexProducerBuilder();
                List<Producer> hexProducers = hexProducerBuilder.build(producerConfigs);

                for (Producer hexProducer : hexProducers) {
                    ProducerRecord<String, JsonNode> hexProducerRecord = new ProducerRecord<>(topicHex, jsonNode);
                    LOG.info(imageHex.getHex());
//                    hexProducer.send(hexProducerRecord);
                }

                getSender().tell(Message.TOPIC_MESSAGE_SENT, getSelf());
            }

        }).build();
    }
}
