package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataCounter {
    private long mapCount;
    private long symbolImageCount;
    private long symbolPositionCount;
    private long rasterLayerCount;
    private long vectorLayerCount;
    private long cadrgLayerCount;
    private long layersProxyCount;
    private long layersSelectedProxyCount;
    private long sourcesProxyCount;
    private long sourcesSelectedProxyCount;
    private long cachesProxyCount;
    private long cachesSelectedProxyCount;
    private List<Map<String, Long>> userCount;

    public DataCounter() {
        this.userCount = new ArrayList<>();
    }

    public DataCounter(long mapCount, long symbolImageCount, long symbolPositionCount, long rasterLayerCount, long vectorLayerCount, long cadrgLayerCount, long layersProxyCount, long layersSelectedProxyCount, long sourcesProxyCount, long sourcesSelectedProxyCount, long cachesProxyCount, long cachesSelectedProxyCount, List<Map<String, Long>> userCount) {
        this.mapCount = mapCount;
        this.symbolImageCount = symbolImageCount;
        this.symbolPositionCount = symbolPositionCount;
        this.rasterLayerCount = rasterLayerCount;
        this.vectorLayerCount = vectorLayerCount;
        this.layersProxyCount = layersProxyCount;
        this.cadrgLayerCount = cadrgLayerCount;
        this.layersSelectedProxyCount = layersSelectedProxyCount;
        this.sourcesProxyCount = sourcesProxyCount;
        this.sourcesSelectedProxyCount = sourcesSelectedProxyCount;
        this.cachesProxyCount = cachesProxyCount;
        this.cachesSelectedProxyCount = cachesSelectedProxyCount;
        this.userCount = userCount;
    }
}
