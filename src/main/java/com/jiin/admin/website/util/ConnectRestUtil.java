package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

@Slf4j
public class ConnectRestUtil {
    public static String sendREST(String restURL, String parameter, String method, String requestJSON){
        String line = null;
        StringBuffer res = new StringBuffer();
        try {
            URL url = new URL(restURL + parameter);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "application/json");

            if(!method.equals("GET") && requestJSON != null){
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestJSON.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while((line = in.readLine()) != null) {
                res.append(line);
            }
            conn.disconnect();
        } catch (ProtocolException e) {
            log.error("REST API 접속 : Protocol 에러. -> " + e.getMessage());
        } catch (IOException e) {
            log.error("REST API 접속 : 입출력 에러. -> " + e.getMessage());
        }

        return res.toString();
    }
}
