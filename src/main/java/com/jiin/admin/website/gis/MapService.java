package com.jiin.admin.website.gis;

import com.jiin.admin.website.dto.MapData;

import java.util.List;

public interface MapService {
    List<MapData> findAllMapData();
    MapData findByName(String name);
}
