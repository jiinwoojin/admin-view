package com.jiin.admin.website.gis;

import com.jiin.admin.website.dto.LayerData;
import com.jiin.admin.website.dto.MapData;

import java.util.List;

public interface MapService {
    List<MapData> findAllMapData();
    List<LayerData> findAllLayerData();
    MapData findByName(String name);
}
