package nz.co.hexgraph.hexcode;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.Configuration;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nz.co.hexgraph.hexcode.HexCodeActor.Message.SEND_HEX_TOPIC_MESSAGE;

public class HexCodeActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexCodeActor.class);

    private Configuration configuration;

    private HexCodeProducer hexCodeProducer;

    private HexCodeMessageBuilder hexCodeMessageBuilder;

    private int rgb;

    public static Props props() {
        return Props.create(HexCodeActor.class);
    }

    public static class UpdateConfiguration {
        private Configuration configuration;

        public UpdateConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }
    }

    public static class UpdateHexCodeProducer {
        private HexCodeProducer hexCodeProducer;

        public UpdateHexCodeProducer(HexCodeProducer hexCodeProducer) {
            this.hexCodeProducer = hexCodeProducer;
        }
    }


    public static class UpdateHexCodeMessageBuilder {
        private HexCodeMessageBuilder hexCodeMessageBuilder;

        public UpdateHexCodeMessageBuilder(HexCodeMessageBuilder hexCodeMessageBuilder) {
            this.hexCodeMessageBuilder = hexCodeMessageBuilder;
        }
    }

    public static class UpdateRgb {
        private int rgb;

        public UpdateRgb(int rgb) {
            this.rgb = rgb;
        }
    }

    public enum Message {
        SEND_HEX_TOPIC_MESSAGE
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateConfiguration.class, r -> this.configuration = r.configuration)
                .match(UpdateHexCodeProducer.class, r -> this.hexCodeProducer = r.hexCodeProducer)
                .match(UpdateHexCodeMessageBuilder.class, r -> this.hexCodeMessageBuilder = r.hexCodeMessageBuilder)
                .match(UpdateRgb.class, r -> this.rgb = r.rgb)
                .matchEquals(SEND_HEX_TOPIC_MESSAGE, r -> {
                    int red = getRedValue(rgb);
                    int green = getGreenValue(rgb);
                    int blue = getBlueValue(rgb);

                    String hexCode = String.format("#%02x%02x%02x", red, green, blue);

                    LOGGER.info(hexCode);
                    HexCodeMessage hexCodeMessage = hexCodeMessageBuilder.withHexCode(hexCode).build();
                    byte[] hexCodeBytes = new ObjectMapper().writeValueAsBytes(hexCodeMessage.getHexCodeMessageValue());

                    String hexCodeTopic = configuration.getTopicHexCode();
                    ProducerRecord<String, byte[]> hexCodeProducerRecord = new ProducerRecord<>(hexCodeTopic, hexCodeMessage.getImagePath(), hexCodeBytes);
                    hexCodeProducer.send(hexCodeProducerRecord);
                }).build();

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
