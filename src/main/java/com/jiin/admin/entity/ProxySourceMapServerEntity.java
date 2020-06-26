package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "_PROXY_SOURCE_MAPSERVER")
@DiscriminatorValue("mapserver")
@Getter
@Setter
public class ProxySourceMapServerEntity extends ProxySourceEntity {
    @Column(name = "REQUEST_MAP", nullable = false)
    private String requestMap;

    @Column(name = "REQUEST_LAYERS", nullable = false)
    private String requestLayers;

    @Column(name = "MAP_SERVER_BINARY", nullable = false)
    private String mapServerBinary;

    @Column(name = "MAP_SERVER_WORK_DIR", nullable = false)
    private String mapServerWorkDir;
}
