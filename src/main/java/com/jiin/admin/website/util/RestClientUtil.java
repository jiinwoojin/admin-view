package com.jiin.admin.website.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RestClientUtil {
    public static Map<String, Object> getREST(boolean secured, String ip, String path, String query){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get;
        if(!StringUtils.isBlank(query)) {
            get = new HttpGet(String.format("%s://%s%s?%s", secured ? "https" : "http", secured ? ip : ip + ":11110", path, query));
        } else {
            get = new HttpGet(String.format("%s://%s%s", secured ? "https" : "http", secured ? ip : ip + ":11110", path));
        }
        try {
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode() == 200){
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(body, Map.class);
            }
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return new HashMap<>();
    }

    public static Map<String, Object> postREST(boolean secured, String ip, String path, Map<String, String> data){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(String.format("%s://%s%s", secured ? "https" : "http", secured ? ip : ip + ":11110", path));

        ObjectMapper mapper = new ObjectMapper();
        try {
            post.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(new StringEntity(mapper.writeValueAsString(data)));
        } catch (JsonProcessingException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("ERROR - " + e.getMessage());
        }

        try {
            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == 200){
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                return mapper.readValue(body, Map.class);
            }
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return new HashMap<>();
    }
}
