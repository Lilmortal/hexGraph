package nz.co.hexgraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import nz.co.hexgraph.config.Configuration;
import nz.co.hexgraph.manager.ManagerActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexGraphInitialization {
    public static final Logger LOGGER = LoggerFactory.getLogger(HexGraphInitialization.class);

    private Configuration configuration;

    public HexGraphInitialization(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        ActorSystem system = ActorSystem.create("hexGraph");

        ActorRef managerActor = system.actorOf(ManagerActor.props(), "managerActor");
        managerActor.tell(new ManagerActor.UpdateConfiguration(configuration), managerActor);
        managerActor.tell(ManagerActor.MESSAGE.START, managerActor);
    }
}
