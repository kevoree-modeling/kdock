package kdock;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.memory.manager.internal.KInternalDataManager;
import org.kevoree.modeling.scheduler.KScheduler;
import org.kevoree.modeling.scheduler.impl.DirectScheduler;

/**
 * Created by gnain on 20/01/16.
 */
public class RestAgentTest {

    public static void main(String[] args) {
        RestAgent agent = new RestAgent("ws://localhost:8080/default","http://kloud1:4243");
        agent.start();

        /*
        RestAgent agent2 = new RestAgent("ws://localhost:8081/default","http://kloud2:4243");
        agent2.start();
        */

    }

}
