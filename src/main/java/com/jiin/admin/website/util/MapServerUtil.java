package com.jiin.admin.website.util;

import com.jiin.admin.Constants;
import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;

import java.io.*;
import java.util.List;

// MapServer 기반 파일 작성 메소드 모음
public class MapServerUtil {
    /**
     * abc.map 파일 내용 생성 메소드
     * @param defaultMap File, dataPath String, map MapEntity, layers List(LayerEntity)
     * @throws IOException Exception
     * @note Entity -> DTO 변경 가능성 고려.
     */
    public String fetchMapFileContextWithEntity(File defaultMap, String dataPath, MapEntity map, List<LayerEntity> layers) throws IOException {
        StringBuilder fileContext = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(defaultMap));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("NAME_VALUE")) {
                line = line.replaceAll("NAME_VALUE", map.getName());
            } else if (line.contains("EXTENT")) {
                line = line.replaceAll("MIN_X", map.getMinX())
                        .replaceAll("MIN_Y", map.getMinY())
                        .replaceAll("MAX_X", map.getMaxX())
                        .replaceAll("MAX_Y", map.getMaxY());
            } else if (line.contains("IMAGETYPE_VALUE")) {
                line = line.replaceAll("IMAGETYPE_VALUE", map.getImageType());
            } else if (line.contains("UNITS_VALUE")) {
                line = line.replaceAll("UNITS_VALUE", map.getUnits());
            } else if (line.contains("SHAPEPATH_VALUE")) {
                line = line.replaceAll("SHAPEPATH_VALUE", dataPath + Constants.DATA_PATH);
            } else if (line.contains("PROJECTION_VALUE")) {
                line = line.replaceAll("PROJECTION_VALUE", map.getProjection());
            } else if (line.contains("IMAGEPATH_VALUE")) {
                line = line.replaceAll("IMAGEPATH_VALUE", dataPath + "/tmp");
            } else if (line.contains("WMS_TITLE_VALUE")) {
                line = line.replaceAll("WMS_TITLE_VALUE", map.getName());
            } else if (line.contains("WMS_SRS_VALUE")) {
                line = line.replaceAll("WMS_SRS_VALUE", map.getProjection());
            } else if (line.contains("LAYER_INCLUDE")) {
                StringBuffer sb = new StringBuffer();
                for(LayerEntity layer : layers){
                    sb.append("  INCLUDE \"./layer/").append(layer.getName()).append(Constants.LAY_SUFFIX).append("\"");
                    sb.append(System.lineSeparator());
                }
                line = sb.toString();
            }

            fileContext.append(line);
            fileContext.append(System.lineSeparator());
        }

        return fileContext.toString();
    }

    /**
     * abc.layer 파일 내용 생성 메소드
     * @param defaultLayer File, dataPath String, layer LayerEntity, layers List(LayerEntity)
     * @throws IOException Exception
     * @note Entity -> DTO 변경 가능성 고려.
     */
    public String fetchLayerFileContextWithEntity(File defaultLayer, String dataPath, LayerEntity layer) throws IOException {
        StringBuilder fileContext = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(defaultLayer));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("NAME_VALUE")) {
                line = line.replaceAll("NAME_VALUE", layer.getName());
            } else if (line.contains("TYPE_VALUE")) {
                line = line.replaceAll("TYPE_VALUE", layer.getType());
            } else if (line.contains("PROJECT_VALUE")) {
                line = line.replaceAll("PROJECT_VALUE", layer.getProjection());
            } else if (line.contains("DATA_VALUE")) {
                line = line.replaceAll("DATA_VALUE", dataPath + layer.getDataFilePath());
            }

            fileContext.append(line);
            fileContext.append(System.lineSeparator());
        }

        return fileContext.toString();
    }
}
