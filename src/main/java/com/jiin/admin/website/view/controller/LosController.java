package com.jiin.admin.website.view.controller;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LOS 이지훈
 *
 */
@Controller
@RequestMapping("los")
public class LosController {

    @Value("${project.los.host}")
    private String losHost;

    @Value("${project.los.port}")
    private String losPort;

    @Value("${project.los.accessKey}")
    private String accessKey;

    @Value("${project.los.secretKey}")
    private String secretKey;

    /**
     * 테스트용 호출기
     * @param param
     * @return
     */
    @ResponseBody
    @PostMapping("test")
    public ConcurrentHashMap<String, Object> testLos(@RequestBody ConcurrentHashMap<String, Object> param) {
        ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<String, Object>();

        //테스트용 데이터
        result.put("buckets", "los");	//minio 의 저장소 명
        result.put("fileNm", "logo.png");	//minio 의 파일명
        result.put("textNm", "test.txt");	//minio 의 파일명

        //result.put("observer_lon", param.get("observer_lon"));
        //result.put("observer_lat", param.get("observer_lat"));
        //result.put("west", param.get("observer_lon"));
        //result.put("south", param.get("observer_lat"));
        //result.put("east", Float.parseFloat(param.get("observer_lon").toString())+0.01);
        //result.put("north", Float.parseFloat(param.get("observer_lat").toString())+0.01);

        return result;
    }

    /**
     * minio 에서 이미지 url 받아오기
     * @param param
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @ResponseBody
    @PostMapping("get")
    public ConcurrentHashMap<String, Object> getLos(@RequestBody ConcurrentHashMap<String, Object> param) throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException {

        ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<>();

        String minioUrl = losHost + ":" + losPort + "/";

        try {
            //minio주소, ID, PW
            MinioClient minioClient = new MinioClient(minioUrl, accessKey, secretKey);

            //minio 파일 검색
            Iterable<Result<Item>> myObjects = minioClient.listObjects(param.get("buckets").toString(), param.get("fileNm").toString());

            //이미지 없을 경우 "" 리턴
            int cnt = -1;
            for (Result<Item> rCnt : myObjects) {
                cnt++;
            }

            if(cnt > -1) {

                //이미지 url
                String url = minioClient.presignedGetObject(param.get("buckets").toString(), param.get("fileNm").toString(), 1);
                result.put("url", url);

                //text 파일
                InputStream text = minioClient.getObject(param.get("buckets").toString(), param.get("textNm").toString(), 1);
                String strText = IOUtils.toString(text, StandardCharsets.UTF_8);
                String arr[] = strText.split(",");

                result.put("west", arr[0]);
                result.put("south", arr[1]);
                result.put("east", arr[2]);
                result.put("north", arr[3]);

                //테스트용 데이터
                //result.put("west", param.get("observer_lon"));
                //result.put("south", param.get("observer_lat"));
                //result.put("east", Float.parseFloat(param.get("observer_lon").toString())+0.01);
                //result.put("north", Float.parseFloat(param.get("observer_lat").toString())+0.01);
            }else {
                result.put("url", "");
            }
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }



        return result;
    }
}