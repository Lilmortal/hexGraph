package nz.co.hexgraph.hex;

import akka.actor.AbstractActor;
import akka.actor.Props;
import nz.co.hexgraph.image.ImageActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import static nz.co.hexgraph.hex.HexActor.Message.SEND_HEX_TOPIC_MESSAGE;

public class HexActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexActor.class);

    private BufferedImage image;

    private int from;

    private int to;

    public static Props props() {
        return Props.create(HexActor.class);
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
        SEND_HEX_TOPIC_MESSAGE,
        TOPIC_MESSAGE_SENT
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateImage.class, r -> this.image = r.image)
                .match(UpdatePosition.class, r -> {
                    this.from = r.from;
                    this.to = r.to;
                })
                .matchEquals(SEND_HEX_TOPIC_MESSAGE, r -> {
                    int width = image.getWidth();

                    for (int i = from; i < to; i++) {
                        int x = getXPosition(i, width);
                        int y = getYPosition(i, width);

                        int rgb = 0;
                        try {
                            rgb = image.getRGB(x, y);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LOGGER.error(e.getMessage());
                            LOGGER.error("Array out of bounds with x:" + x + ", y:" + y);
                        }

                        int red = getRedValue(rgb);
                        int green = getGreenValue(rgb);
                        int blue = getBlueValue(rgb);

                        String hex = String.format("#%02x%02x%02x", red, green, blue);

                        getSender().tell(new ImageActor.UpdateHexValue(hex), getSelf());
                    }
                }).build();
    }

    private int getXPosition(int position, int imageWidth) {
        if (position < imageWidth) {
            return position;
        }
        return position % imageWidth;
    }

    private int getYPosition(int position, int imageWidth) {
        if (position < imageWidth) {
            return 0;
        }
        return position / imageWidth;
    }

    private int getRedValue(int rgb) {
        return (rgb >> 16) & 0x000000FF;
    }

    private int getGreenValue(int rgb) {
        return (rgb >> 8) & 0x000000FF;
    }

    private int getBlueValue(int rgb) {
        return (rgb) & 0x000000FF;
    }
}
