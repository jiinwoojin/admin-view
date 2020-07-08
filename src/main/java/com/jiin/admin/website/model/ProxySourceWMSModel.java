package com.jiin.admin.website.model;

import com.jiin.admin.dto.ProxySourceWMSDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceWMSModel {
    private long id;
    private String method;
    private String name;
    private String type;
    private Integer concurrentRequests;
    private String wmsOptsVersion;
    private Integer httpClientTimeout;
    private String requestUrl;
    private String requestMap;
    private String requestLayers;
    private Boolean requestTransparent;
    private String supportedSrs;

    public ProxySourceWMSModel() {
        this.method = "INSERT";
        this.type = "wms";
    }

    public ProxySourceWMSModel(long id, String name, String type, Integer concurrentRequests, String wmsOptsVersion, Integer httpClientTimeout, String requestURL, String requestMap, String requestLayers, Boolean requestTransparent, String supportedSRS) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.concurrentRequests = concurrentRequests;
        this.wmsOptsVersion = wmsOptsVersion;
        this.httpClientTimeout = httpClientTimeout;
        this.requestUrl = requestURL;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.requestTransparent = requestTransparent;
        this.supportedSrs = supportedSRS;
    }

    public static ProxySourceWMSDTO convertDTO(ProxySourceWMSModel model) {
        if (model == null) return null;
        return new ProxySourceWMSDTO(model.getId(), model.getName(), model.getType(), false, false, model.getConcurrentRequests(), model.getWmsOptsVersion(), model.getHttpClientTimeout(), model.getRequestUrl(), model.getRequestMap(), model.getRequestLayers(), model.getRequestTransparent(), model.getSupportedSrs());
    }
}
