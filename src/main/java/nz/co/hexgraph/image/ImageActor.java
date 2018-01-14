package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class ImageActor extends AbstractActor {
    public static Logger LOG = LoggerFactory.getLogger(ImageActor.class);

    private BufferedImage image;

    public static Props props() {
        return Props.create(ImageActor.class);
    }

    public static class UpdateImage {
        private BufferedImage image;

        public UpdateImage(BufferedImage image) {
            this.image = image;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(UpdateImage.class, r -> {
            this.image = r.image;

            int processors = Runtime.getRuntime().availableProcessors();
            int numberOfPixels = image.getWidth() * image.getHeight();

            LOG.info("Processors: " + processors + " " + numberOfPixels);
            // TODO: Pass X and Y despite numberOfPixels
            int pos = 10;
            for (int i = 0; i < 3; i++) {
                ActorRef pixelActor = getContext().actorOf(PixelActor.props());
                pixelActor.tell(new PixelActor.UpdateImage(image), getSelf());
                pixelActor.tell(new PixelActor.UpdatePosition(0, pos), getSelf());
                pos += 10;
            }
        }).build();
    }
}
