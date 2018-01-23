package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.consumers.ConsumerPropertiesBuilder;
import nz.co.hexgraph.consumers.HexGraphConsumer;
import nz.co.hexgraph.consumers.HexGraphConsumerConfig;
import nz.co.hexgraph.hexvalue.HexProducerFactory;
import nz.co.hexgraph.hexvalue.HexValueProducer;
import nz.co.hexgraph.image.ImageActor;
import nz.co.hexgraph.consumers.ConsumerValue;
import nz.co.hexgraph.image.ImagesConsumer;
import nz.co.hexgraph.manager.ManagerActor;
import nz.co.hexgraph.producer.HexGraphProducer;
import nz.co.hexgraph.producer.HexGraphProducerConfig;
import nz.co.hexgraph.producer.ProducerPropertiesBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static nz.co.hexgraph.image.ImageActor.MESSAGE.UPDATE_PIXEL_COUNTS;

public class HexGraphInitialization {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    private Configuration configuration;

    public HexGraphInitialization(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        ActorSystem system = ActorSystem.create("hexGraph");

        ActorRef managerActor = system.actorOf(ManagerActor.props(), "managerActor");

    }


}
