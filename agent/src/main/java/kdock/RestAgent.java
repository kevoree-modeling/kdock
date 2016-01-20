package kdock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import kdock.meta.MetaContainer;
import kdock.meta.MetaHost;
import kdock.meta.MetaMetric;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.defer.KDefer;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.plugin.WebSocketClientPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gnain on 20/01/16.
 */
public class RestAgent {

    private String _targetHost;
    private String _kmfAddress;

    private KdockModel model;


    private ScheduledExecutorService executor;

    public RestAgent(String kmfAddress, String targetHost) {
        _targetHost = targetHost;
        _kmfAddress = kmfAddress;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {

        WebSocketClientPlugin webSocketPlugin = new WebSocketClientPlugin(_kmfAddress);
        model = new KdockModel(DataManagerBuilder.create().withContentDeliveryDriver(webSocketPlugin).build());
        model.connect(new KCallback<Throwable>() {
            @Override
            public void on(Throwable throwable) {

                executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        updateHostInfos();
                        model.save(new KCallback() {
                            @Override
                            public void on(Object o) {

                            }
                        });
                    }
                }, 1, 1, TimeUnit.SECONDS);
            }
        });
    }

    private void updateHostInfos() {
        System.out.println("Update host");
        long currentTs = System.currentTimeMillis();
        JsonObject infos = getHostInfo();


        model.find(MetaHost.getInstance(), 0, currentTs, "name=" + infos.get("Name").asString(), new KCallback<KObject>() {
            @Override
            public void on(KObject kObject) {
                Host h;
                if (kObject == null) {
                    System.out.println("Host not found");
                    h = model.createHost(0, currentTs).setName(infos.get("Name").asString());
                } else {
                    h = (Host) kObject;
                    System.out.println("Host: " + h);
                }

                JsonArray containersInfo = getContainersInfo();

                for (JsonValue containerValue : containersInfo.values()) {
                    JsonObject containerInfos = containerValue.asObject();

                    h.traversal().traverse(MetaHost.REL_CONTAINERS).withAttribute(MetaContainer.ATT_ID, containerInfos.get("Id").asString()).then(new KCallback<KObject[]>() {
                        @Override
                        public void on(KObject[] kObjects) {

                            Container container;

                            if(kObjects.length == 0) {
                                container = model.createContainer(0, currentTs).setId(containerInfos.get("Id").asString()).setName(containerInfos.get("Names").asArray().get(0).asString());
                                h.addContainers(container);
                            } else {
                                container = (Container)kObjects[0];
                            }

                            JsonObject metrics = getContainerMetrics(container.getName());
                            updateMetrics(metrics, container, currentTs);
                        }
                    });
                }
            }
        });
    }

    private void updateMetrics(JsonObject metrics, Container container, long currentTs) {
        for(String name : metrics.names()) {

            if(metrics.get(name).isObject()) {
                updateMetrics(metrics.get(name).asObject(), container, currentTs);
            } else {
                container.traversal().traverse(MetaContainer.REL_METRICS).withAttribute(MetaMetric.ATT_NAME,name).then(new KCallback<KObject[]>() {
                    @Override
                    public void on(KObject[] kObjects) {
                        Metric m;
                        try {
                            if (kObjects.length == 0) {
                                m = model.createMetric(0, currentTs).setName(name).setValue(metrics.get(name).asDouble());
                                container.addMetrics(m);
                            } else {
                                m = model.createMetric(0, currentTs).setName(name).setValue(metrics.get(name).asDouble());
                            }
                        }catch(UnsupportedOperationException e) {
                            System.err.println("Could not convert attribute '" + name + "' to number: " + metrics.get(name));
                        }
                    }
                });
            }
        }
    }


    private JsonObject getHostInfo() {
        return getJsonObject(_targetHost + "/info");
    }


    private JsonArray getContainersInfo() {
        return getJsonArray(_targetHost + "/containers/json");
    }

    private JsonObject getContainerMetrics(String name) {
        return getJsonObject(_targetHost + "/containers"+name+"/stats?stream=0");
    }

    private JsonObject getJsonObject(String url_src) {
        try {

            URL url = new URL(url_src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            return JsonObject.readFrom(new InputStreamReader(conn.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            return JsonObject.readFrom("{failed:\"" + e.getMessage() + "\"}");
        }
    }



    private JsonArray getJsonArray(String url_src) {
        try {

            URL url = new URL(url_src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            return JsonArray.readFrom(new InputStreamReader(conn.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            return JsonArray.readFrom("{failed:\"" + e.getMessage() + "\"}");
        }
    }


}
