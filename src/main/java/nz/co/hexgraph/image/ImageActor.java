package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.config.FileType;
import nz.co.hexgraph.pixel.PixelActor;
import nz.co.hexgraph.reader.FileReader;
import nz.co.hexgraph.reader.Reader;
import nz.co.hexgraph.reader.S3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;

public class ImageActor extends AbstractActor {
    public static Logger LOG = LoggerFactory.getLogger(ImageActor.class);

    private String imagePath;

    public static Props props() {
        return Props.create(ImageActor.class);
    }

    public static class UpdateImagePath {
        private String imagePath;

        public UpdateImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(UpdateImagePath.class, r -> {
            this.imagePath = r.imagePath;

            Configuration configuration = Configuration.getInstance();
            FileType fileType = configuration.getImageFileType();

            Reader reader = null;
            switch (fileType) {
                case FILE:
                    reader = new FileReader();
                    break;
                case S3:
                    reader = new S3Reader();
                    break;
                default:
                    // TODO: throw error
            }

            File imageFile = new File(imagePath);
            BufferedImage image = reader.getImage(imageFile);
            LocalDateTime imageCreationDate = reader.getCreationDate(imageFile);
            ImageInfo imageInfo = new ImageInfo(imagePath, image, imageCreationDate);

            int processors = Runtime.getRuntime().availableProcessors();
            int numberOfPixels = image.getWidth() * image.getHeight();

            int numberOfPixelsPerProcessor = numberOfPixels / processors;

            for (int i = 1; i <= processors; i++) {
                int pos = numberOfPixelsPerProcessor * i;
                ActorRef pixelActor = getContext().actorOf(PixelActor.props());
                pixelActor.tell(new PixelActor.UpdateImageInfo(imageInfo), getSelf());
                pixelActor.tell(new PixelActor.UpdateImage(image), getSelf());
                pixelActor.tell(new PixelActor.UpdatePosition(pos - numberOfPixelsPerProcessor, pos), getSelf());
            }
        }).matchEquals(PixelActor.Message.TOPIC_MESSAGE_SENT, r -> {
            LOG.info("Message sent to topic.");
        }).build();
    }
}
