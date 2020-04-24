package com.jiin.admin.website.server.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DataCounter {
    private long mapCount;
    private long symbolCount;
    private long rasterLayerCount;
    private long vectorLayerCount;
    private long layersProxyCount;
    private long layersSelectedProxyCount;
    private long sourcesProxyCount;
    private long sourcesSelectedProxyCount;
    private long cachesProxyCount;
    private long cachesSelectedProxyCount;
    private Map<String, Long> userCount;

    public DataCounter(){
        this.userCount = new HashMap<>();
    }

    public DataCounter(long mapCount, long symbolCount, long rasterLayerCount, long vectorLayerCount, long layersProxyCount, long layersSelectedProxyCount, long sourcesProxyCount, long sourcesSelectedProxyCount, long cachesProxyCount, long cachesSelectedProxyCount, Map<String, Long> userCount) {
        this.mapCount = mapCount;
        this.symbolCount = symbolCount;
        this.rasterLayerCount = rasterLayerCount;
        this.vectorLayerCount = vectorLayerCount;
        this.layersProxyCount = layersProxyCount;
        this.layersSelectedProxyCount = layersSelectedProxyCount;
        this.sourcesProxyCount = sourcesProxyCount;
        this.sourcesSelectedProxyCount = sourcesSelectedProxyCount;
        this.cachesProxyCount = cachesProxyCount;
        this.cachesSelectedProxyCount = cachesSelectedProxyCount;
        this.userCount = userCount;
    }
}
