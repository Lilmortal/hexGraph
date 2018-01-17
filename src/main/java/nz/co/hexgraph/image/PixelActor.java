package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.camera.CameraProducer;
import nz.co.hexgraph.config.CameraConfigProducer;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.partitioner.CameraPartitioner;
import nz.co.hexgraph.producers.Producer;
import nz.co.hexgraph.producers.ProducerPropertiesBuilder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

            LOG.info(width + "");
            for (int i = from; i < to; i++) {
                int x, y;

                if (i < width) {
                    x = i;
                    y = 0;
                } else {
                    y = i / width;
                    x = i % width;
                }

                // TODO: Have a message that get red, blue, green
                // Here, it's pixel[fromX][fromY] to pixel[toX][toY], create a new class with X and Y
                int rgb = 0;
                try {
                    rgb = image.getRGB(x, y);
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOG.error(e.getMessage());
                    LOG.error("Array out of bounds with x:" + x + ", y:" + y);
                }
                // TODO: split 2d array into 4 1d arrays
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;

                String hex = String.format("#%02x%02x%02x", red, green, blue);

                ImageHex imageHex = new ImageHex(imageInfo, hex);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.valueToTree(imageHex);

                Configuration configuration = Configuration.getInstance();

                List<CameraConfigProducer> cameraConfigProducers = configuration.getCameraConfigProducers();
                Map<String, String> partitionConfig = configuration.getPartitions();
                String topicHex = configuration.getTopicHex();

                for (CameraConfigProducer cameraConfigProducer : cameraConfigProducers) {
                    Properties producerProperties = buildProducerProperties(cameraConfigProducer, partitionConfig);
                    Producer cameraProducer = new CameraProducer(producerProperties);

                    ProducerRecord<String, JsonNode> producerRecord = new ProducerRecord<>(topicHex, jsonNode);
                    LOG.info(imageHex.getHex());
//                    cameraProducer.send(producerRecord);
                }
            }

        }).build();
    }

    private Properties buildProducerProperties(CameraConfigProducer cameraConfigProducer, Map<String, String> partitionConfig) {
        ProducerPropertiesBuilder producerPropertiesBuilder = new ProducerPropertiesBuilder(cameraConfigProducer.getBootstrapServerConfig(),
                cameraConfigProducer.getSerializerClassConfig(),
                cameraConfigProducer.getValueSerializerClassConfig());

        return producerPropertiesBuilder.withPartitionerClassConfig(CameraPartitioner.class.getCanonicalName()).withPartitions(partitionConfig).build();
    }
}
