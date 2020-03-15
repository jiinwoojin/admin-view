package com.jiin.admin.website.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapProxyUtil {
    private static final String HOST = "211.172.246.71";
    private static final String PORT = "12000";

    public static Map<String, Object> getCapabilities(){
        final String reqURL = String.format("http://%s:%s/service?REQUEST=GetCapabilities", HOST, PORT);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(reqURL);

        try {
            HttpResponse response = client.execute(getRequest);

            if(response.getStatusLine().getStatusCode() == 200){
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONObject json = XML.toJSONObject(body);
                return json.toMap();
            } else {
                System.out.println("Response Error : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static String getServiceURL(){
        return String.format("http://%s:%s", HOST, PORT);
    }
}
