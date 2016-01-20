package kdock;

/**
 * Created by gnain on 20/01/16.
 */
public class RestAgentTest {

    public static void main(String[] args) {
        RestAgent agent = new RestAgent("localhost","http://kloud1:4243");
        agent.start();
    }

}
