package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "_PROXY_SOURCE_WMS")
@DiscriminatorValue("wms")
@Getter
@Setter
public class ProxySourceWMSEntity extends ProxySourceEntity {
    @Column(name = "CONCURRENT_REQUESTS", nullable = false)
    private Integer concurrentRequests;

    @Column(name = "WMS_OPTS_VERSION", nullable = false)
    private String wmsOptsVersion;

    @Column(name = "HTTP_CLIENT_TIMEOUT", nullable = false)
    private Integer httpClientTimeout;

    @Column(name = "REQUEST_URL", nullable = false)
    private String requestURL;

    @Column(name = "REQUEST_LAYERS", nullable = false)
    private String requestLayers;

    @Column(name = "REQUEST_TRANSPARENT", nullable = false)
    private Boolean requestTransparent;

    @Column(name = "SUPPORTED_SRS", nullable = false)
    private String supportedSRS;
}
