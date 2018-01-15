package nz.co.hexgraph.image;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class PixelActor extends AbstractActor {
    public static final Logger LOG = LoggerFactory.getLogger(PixelActor.class);

    private BufferedImage image;

    private int from;

    private int to;

    public static Props props() {
        return Props.create(PixelActor.class);
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
        return receiveBuilder().match(UpdateImage.class, r -> {
            this.image = r.image;
        }).match(UpdatePosition.class, r -> {
            this.from = r.from;
            this.to = r.to;

            // TODO: Have a message that get red, blue, green
            // Here, it's pixel[fromX][fromY] to pixel[toX][toY], create a new class with X and Y
            int rgb = image.getRGB(from, to);

            int width = image.getWidth();
            int height = image.getHeight();
            image.
            // TODO: split 2d array into 4 1d arrays
            int red = (rgb >> 16) & 0x000000FF;
            int green = (rgb >>8 ) & 0x000000FF;
            int blue = (rgb) & 0x000000FF;

            LOG.info(red + " " + green + " " + blue);
        }).build();
    }
}
