package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.hex.HexActor;
import nz.co.hexgraph.producers.HexGraphProducer;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.ReaderFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static nz.co.hexgraph.hex.HexActor.Message.SEND_HEX_TOPIC_MESSAGE;
import static nz.co.hexgraph.hex.HexActor.Message.TOPIC_MESSAGE_SENT;
import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class ImageActor extends AbstractActor {
    public static Logger LOGGER = LoggerFactory.getLogger(ImageActor.class);

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private String imagePath;

    private FileType fileType;

    private LocalDateTime creationDate;

    private List<HexGraphProducer> hexGraphProducers;

    private String topic;

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

    public static class UpdateFileType {
        private FileType fileType;

        public UpdateFileType(FileType fileType) {
            this.fileType = fileType;
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

    public static class UpdateHexValue {
        private String hexValue;

        public UpdateHexValue(String hexValue) {
            this.hexValue = hexValue;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateImagePath.class, r -> this.imagePath = r.imagePath)
                .match(UpdateFileType.class, r -> this.fileType = r.fileType)
                .match(UpdateProducers.class, r -> this.hexGraphProducers = r.hexGraphProducers)
                .match(UpdateTopic.class, r -> this.topic = r.topic)
                .matchEquals(UPDATE_PIXEL_COUNTS, r -> {
                    Reader reader = ReaderFactory.create(fileType);

                    BufferedImage image = reader.getImage(imagePath);
                    this.creationDate = reader.getCreationDate(imagePath);

                    int numberOfPixels = getNumberOfPixels(image);

                    LOGGER.info("There are " +  numberOfPixels + " pixels split between " + PROCESSORS + " processors.");

                    int numberOfPixelsPerProcessor = getNumberOfPixelsPerProcessor(numberOfPixels);

                    // TODO: Test performance compare to just one thread handling all the pixels in an image
                    for (int i = 1; i <= PROCESSORS; i++) {
                        int pos = numberOfPixelsPerProcessor * i;
                        ActorRef hexActor = getContext().actorOf(HexActor.props());
                        hexActor.tell(new HexActor.UpdateImage(image), getSelf());
                        hexActor.tell(new HexActor.UpdatePosition(pos - numberOfPixelsPerProcessor, pos), getSelf());
                        hexActor.tell(SEND_HEX_TOPIC_MESSAGE, getSelf());
                    }
                }).match(UpdateHexValue.class, r -> {
                    LOGGER.info("Image with path " + imagePath + " has hex value as " + r.hexValue);

                    ImageMessage imageMessage = new ImageMessage(imagePath, creationDate, r.hexValue);
                    byte[] imagePixelBytes = new ObjectMapper().writeValueAsBytes(imageMessage);

                    for (HexGraphProducer hexGraphProducer : hexGraphProducers) {
                        ProducerRecord<String, byte[]> hexProducerRecord = new ProducerRecord<>(topic, imagePixelBytes);
                        hexGraphProducer.send(hexProducerRecord);
                    }
                }).build();
    }

    private int getNumberOfPixels(BufferedImage image) {
        return image.getWidth() * image.getHeight();
    }

    private int getNumberOfPixelsPerProcessor(int numberOfPixels) {
        return numberOfPixels / PROCESSORS;
    }
}
