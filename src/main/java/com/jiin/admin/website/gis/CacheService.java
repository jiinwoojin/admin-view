package com.jiin.admin.website.gis;

import java.util.Map;

public interface CacheService {
    Map<String, Object> getCachedRequestData();
    Map<String, Object> getCachedLayerData();
    Map<String, Object> getBoundingBoxInfoWithCrs();
}
