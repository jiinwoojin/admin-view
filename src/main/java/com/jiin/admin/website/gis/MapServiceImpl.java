package com.jiin.admin.website.gis;

import com.jiin.admin.website.dto.LayerData;
import com.jiin.admin.website.dto.MapData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MapServiceImpl implements MapService {
    private static final MapData SAMPLE_MAP1 = new MapData(
"MAP_1", true, 640, 480, "EPSG:4326", "KM", 0.0f, 0.0f, 180.0f, 180.0f,
       Arrays.asList(
           new LayerData("LAYER_1_1", "GROUP1", true, "POLYGON", "EPSG:4326", "LOCAL", "", "layer_1_1.shp", 12500.0f, 100000.0f, "", "", "", "LABLE00"),
           new LayerData("LAYER_1_2", "GROUP1", true, "LINE", "EPSG:4326", "LOCAL", "", "layer_1_2.shp", 12500.0f, 100000.0f, "", "", "", "LABLE01"),
           new LayerData("LAYER_1_3", "GROUP1", true, "RASTER", "EPSG:4326", "LOCAL", "", "layer_1_3.tif", 12500.0f, 100000.0f, "", "", "", "LABLE02")
       )
    );

    private static final MapData SAMPLE_MAP2 = new MapData(
"MAP_2", true, 640, 480, "EPSG:4326", "KM", 0.0f, 0.0f, 180.0f, 180.0f,
        Arrays.asList(
            new LayerData("LAYER_2_1", "GROUP2", true, "POLYGON", "EPSG:4326", "POSTGIS", "user=aaa password=****** dbname=dbname host=localhost port=5432", "layer_2_1", 12500.0f, 100000.0f, "", "WHERE id = 10", "", "LABLE10"),
            new LayerData("LAYER_2_2", "GROUP2", true, "LINE", "EPSG:4326", "POSTGIS", "user=aaa password=****** dbname=dbname host=localhost port=5432", "layer_2_2", 12500.0f, 100000.0f, "", "WHERE id = 20", "", "LABLE11"),
            new LayerData("LAYER_2_3", "GROUP2", true, "RASTER", "EPSG:4326", "POSTGIS", "user=aaa password=****** dbname=dbname host=localhost port=5432", "layer_2_3", 12500.0f, 100000.0f, "", "WHERE id = 30", "", "LABLE12")
        )
    );

    @Override
    public List<MapData> findAllMapData() {
        return Arrays.asList(SAMPLE_MAP1, SAMPLE_MAP2);
    }

    @Override
    public MapData findByName(String name) {
        switch(name){
            case "MAP_1" :
                return SAMPLE_MAP1;
            case "MAP_2" :
                return SAMPLE_MAP2;
            default :
                return new MapData();
        }
    }
}
