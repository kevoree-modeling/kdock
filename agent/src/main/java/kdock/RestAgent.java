package kdock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by gnain on 20/01/16.
 */
public class RestAgent {

    private String _targetHost;
    private String _kmfAddress;

    public RestAgent(String kmfAddress, String targetHost) {
        _targetHost = targetHost;
        _kmfAddress = kmfAddress;
    }

    public void start() {

        System.out.println("==== HOST ====");
        System.out.println(getHostInfo());

        System.out.println("==== CONTAINERS ====");
        System.out.println(getContainersInfo());

    }


    private JsonObject getHostInfo() {
        return getJsonObject(_targetHost + "/info");
    }


    private JsonArray getContainersInfo() {
        return getJsonArray(_targetHost + "/containers/json");
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
