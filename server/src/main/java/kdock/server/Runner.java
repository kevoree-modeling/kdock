package kdock.server;

import org.kevoree.log.Log;
import org.kevoree.modeling.plugin.LevelDBPlugin;
import org.kevoree.modeling.plugin.WebSocketGateway;

public class Runner {

    public static void main(String[] args) throws Exception {

        String storagePath = "kdock_data";
        int port = 8080;

        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
            storagePath = args[1];
        }


        LevelDBPlugin storage = new LevelDBPlugin(storagePath);
        final int finalPort = port;
        final String finalStoragePath = storagePath;
        storage.connect(throwable -> {
            Log.info("KDock: Storage started in :{}", finalStoragePath);
            WebSocketGateway gateway = WebSocketGateway.expose(storage, finalPort);
            gateway.start();
            Log.info("KDock: WebSocket gateway started with port :{}", finalPort);
        });

    }

}
