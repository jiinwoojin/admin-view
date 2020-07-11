package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceWMSDTO extends ProxySourceDTO {
    private Integer concurrentRequests;
    private String wmsOptsVersion;
    private Integer httpClientTimeout;
    private String requestURL;
    private String requestMap;
    private String requestLayers;
    private Boolean requestTransparent;
    private String supportedSRS;

    public ProxySourceWMSDTO() {
        super();
    }

    public ProxySourceWMSDTO(Long id, String name, String type, Boolean selected, Boolean isDefault, Integer concurrentRequests, String wmsOptsVersion, Integer httpClientTimeout, String requestURL, String requestMap, String requestLayers, Boolean requestTransparent, String supportedSRS) {
        super(id, name, type, selected, isDefault);
        this.concurrentRequests = concurrentRequests;
        this.wmsOptsVersion = wmsOptsVersion;
        this.httpClientTimeout = httpClientTimeout;
        this.requestURL = requestURL;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.requestTransparent = requestTransparent;
        this.supportedSRS = supportedSRS;
    }
}
