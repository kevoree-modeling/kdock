package kdock;

import kdock.meta.MetaContainer;
import kdock.meta.MetaHost;
import kdock.meta.MetaMetric;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.plugin.WebSocketClientPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gnain on 21/01/16.
 */
public class JavaPuller {


    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        WebSocketClientPlugin webSocketPlugin = new WebSocketClientPlugin("ws://localhost:8080/default");
        KdockModel model = new KdockModel(DataManagerBuilder.create().withContentDeliveryDriver(webSocketPlugin).build());
        model.connect(new KCallback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        readMetrics(model);
                    }
                }, 1, 1, TimeUnit.SECONDS);

            }
        });


    }

    public static void readMetrics(KdockModel model) {

        long currentTs = System.currentTimeMillis();

        model.find(MetaHost.getInstance(), 0, currentTs, "name=kloud1", new KCallback<KObject>() {
            @Override
            public void on(KObject kObject) {
                Host h;
                if (kObject == null) {
                    System.out.println("Reader::Host not found");
                } else {
                    h = (Host) kObject;
                    System.out.println("Host: " + h);
                    h.traversal().traverse(MetaHost.REL_CONTAINERS).traverse(MetaContainer.REL_METRICS).traverse(MetaMetric.REL_METRICS).withAttribute(MetaMetric.ATT_NAME, "system_cpu_usage").traverse(MetaMetric.REL_VALUES).then(new KCallback<KObject[]>() {
                        @Override
                        public void on(KObject[] kObjects) {
                            if (kObjects.length == 0) {
                                System.out.println("No value yet");
                            } else {
                                System.out.println("Value: " + ((Value) kObjects[0]).getValue());
                            }
                        }
                    });
                }
            }
        });

    }

}
