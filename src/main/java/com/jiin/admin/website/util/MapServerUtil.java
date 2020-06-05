package com.jiin.admin.website.util;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

// MapServer 기반 파일 작성 메소드 모음
public class MapServerUtil {
    /**
     * abc.map 파일 내용 생성 메소드
     * @param defaultMap File, dataPath String, map MapEntity, layers List(LayerEntity)
     * @throws IOException Exception
     * @note JPA Entity 대응 필요, wms_srs (복수), wms_title 등 메타 정보 주입 대응 필요
     */
    public static String fetchMapFileContextWithDTO(File defaultMap, String dataPath, MapDTO map, List<LayerDTO> layers) throws IOException {
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
                for(LayerDTO layer : layers){
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
     * @note JPA Entity 대응 필요
     */
    public static String fetchLayerFileContextWithDTO(File defaultLayer, String dataPath, LayerDTO layer) throws IOException {
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

    /**
     * LAYER 데이터 삭제 이후, *.map 파일에서  INCLUDE "./layer/~~.lay" 를 없애는 메소드
     * @Param mapFilePath String, layer String
     * @throws IOException Exception
     * @note
     */
    public static void removeLayerIncludeSyntaxInMapFiles(String mapFilePath, String layer) throws IOException {
        File dir = new File(mapFilePath);
        if(dir.isDirectory()){
            for(File file : dir.listFiles()){
                if(file.getName().lastIndexOf(".map") > -1){
                    String filePath = mapFilePath + "/" + file.getName();
                    String context = FileSystemUtil.fetchFileContext(filePath);
                    context = context.replace(String.format("%s  INCLUDE \"./layer/%s%s\"", System.lineSeparator(), layer, Constants.LAY_SUFFIX), "");
                    FileSystemUtil.createAtFile(filePath, context);
                }
            }
        }
    }
}
