import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RestClientTest {
    public static Map<String, Object> loadGetResponse(String url){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode() == 200){
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return new HashMap<>();
        }
    }

    public static void main(String[] args){
        System.out.println(loadGetResponse("http://192.168.1.141:11110/admin-view/server/api/map/list"));
    }
}
