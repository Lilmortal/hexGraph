package nz.co.hexgraph.hexvalue;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.producer.HexGraphProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nz.co.hexgraph.hexvalue.HexValueActor.Message.SEND_HEX_TOPIC_MESSAGE;

public class HexValueActor extends AbstractActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexValueActor.class);

    private Configuration configuration;

    private HexValueProducer hexValueProducer;

    private HexValueMessageBuilder hexValueMessageBuilder;

    private int rgb;

    public static Props props() {
        return Props.create(HexValueActor.class);
    }

    public static class UpdateConfiguration {
        private Configuration configuration;

        public UpdateConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }
    }

    public static class UpdateHexValueProducer {
        private HexValueProducer hexValueProducer;

        public UpdateHexValueProducer(HexValueProducer hexValueProducer) {
            this.hexValueProducer = hexValueProducer;
        }
    }


    public static class UpdateHexValueMessageBuilder {
        private HexValueMessageBuilder hexValueMessageBuilder;

        public UpdateHexValueMessageBuilder(HexValueMessageBuilder hexValueMessageBuilder) {
            this.hexValueMessageBuilder = hexValueMessageBuilder;
        }
    }

    public static class UpdateRgb {
        private int rgb;

        public UpdateRgb(int rgb) {
            this.rgb = rgb;
        }
    }

    public enum Message {
        SEND_HEX_TOPIC_MESSAGE,
        TOPIC_MESSAGE_SENT
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateConfiguration.class, r -> this.configuration = r.configuration)
                .match(UpdateHexValueProducer.class, r -> this.hexValueProducer = r.hexValueProducer)
                .match(UpdateHexValueMessageBuilder.class, r -> this.hexValueMessageBuilder = r.hexValueMessageBuilder)
                .match(UpdateRgb.class, r -> this.rgb = r.rgb)
                .matchEquals(SEND_HEX_TOPIC_MESSAGE, r -> {
                    int red = getRedValue(rgb);
                    int green = getGreenValue(rgb);
                    int blue = getBlueValue(rgb);

                    String hexValue = String.format("#%02x%02x%02x", red, green, blue);

                    LOGGER.info(hexValue);
                    HexValueMessage hexValueMessage = hexValueMessageBuilder.withHexValue(hexValue).build();
                    byte[] hexValueBytes = new ObjectMapper().writeValueAsBytes(hexValueMessage);

                    String hexValueTopic = configuration.getTopicHexValue();
                    ProducerRecord<String, byte[]> hexValueProducerRecord = new ProducerRecord<>(hexValueTopic, hexValueBytes);
                    hexValueProducer.send(hexValueProducerRecord);
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
