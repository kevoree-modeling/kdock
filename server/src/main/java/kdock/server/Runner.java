package kdock.server;

import org.kevoree.log.Log;
import org.kevoree.modeling.plugin.LevelDBPlugin;
import org.kevoree.modeling.plugin.WebSocketGateway;

public class Runner {

    public static void main(String[] args) throws Exception {

        int port = 8080;
        String storagePath = "kdock_data";

        LevelDBPlugin storage = new LevelDBPlugin(storagePath);
        storage.connect(throwable -> {
            Log.info("KDock: Storage started in :{}", storagePath);
            WebSocketGateway gateway = WebSocketGateway.expose(storage, port);
            gateway.start();
            Log.info("KDock: WebSocket gateway started with port :{}", port);
        });

    }

}
