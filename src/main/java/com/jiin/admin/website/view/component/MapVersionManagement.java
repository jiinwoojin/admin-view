package com.jiin.admin.website.view.component;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.MapVersionMapper;
import com.jiin.admin.website.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapVersionManagement {
    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private MapMapper mapMapper;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private MapVersionMapper mapVersionMapper;

    /**
     * 버전 정리 중 높은 버전 (예를 들어 가장 높은 버전 레이어를 하나 삭제할 때) 이 포함되어 있으면 정리하는 메소드
     */
    private void cleanCurrentVersionData(MapDTO map, double baseVersion){
        List<MapVersionDTO> versions = mapVersionMapper.findByMapId(map.getId());
        for(MapVersionDTO version : versions){
            if(version.getVersion() > baseVersion){
                mapVersionMapper.deleteRelateByVersionId(version.getId());
                mapVersionMapper.deleteById(version.getId());
                String filePath = dataPath + version.getZipFilePath();
                try {
                    FileSystemUtil.deleteFile(filePath);
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            }
        }
    }

    /**
     * 레이어들 중에서 가장 큰 버전을 가져온다.
     * @param layers List Of Layers
     */
    private Double loadMaximumVersionAtLayerList(List<LayerDTO> layers){
        return Collections.max(layers.stream().filter(o -> o.getVersion() != null).map(LayerDTO::getVersion).collect(Collectors.toSet()));
    }

    /**
     * 레이어들 중에서 버전들의 집합체를 불러온다.
     * @param layers List Of Layers
     */
    private Set<Double> loadVersionSetAtLayerList(List<LayerDTO> layers){
        return layers.stream().filter(o -> o.getVersion() != null).map(LayerDTO::getVersion).collect(Collectors.toSet());
    }

    /**
     * MAP 데이터를 추가 및 수정한 이후, 버전 관리 기능을 실시한다.
     * @param mapDTO MapDTO, layers List of Layers
     */
    @Transactional
    public boolean saveMapVersionRecentlyStatus(MapDTO mapDTO, List<LayerDTO> layers) {
        double newVersion = loadMaximumVersionAtLayerList(layers);

        // 종속된 LAYER 중 가장 신 버전이 삭제 되어 버전이 낮아지는 경우에 실행되는 메소드.
        MapVersionDTO current = mapVersionMapper.findByMapIdRecently(mapDTO.getId());
        if(current != null) {
            if(newVersion < current.getVersion()) {
                cleanCurrentVersionData(mapDTO, newVersion);
            }
        }

        // 아래 문장은 1.9 -> 2.0, 2.9 -> 3.0 등으로 넘어갈 때 모든 버전 데이터를 삭제하기 위한 로직에서 활용하면 될 것이다...
        if(newVersion == Math.ceil(newVersion)){
            log.info("새로운 버전이 정수이기 때문에 초기화 됩니다.");
        }

        String savePath = Paths.get(dataPath, Constants.MAP_VERSION_FILE_PATH, mapDTO.getName()).toString();
        String fileName = String.format("%s_V%.1f.zip", mapDTO.getName(), newVersion);

        File zipFile = FileSystemUtil.saveZipFileWithPaths(dataPath, savePath, fileName, layers.stream().map(o -> new HashMap<String, String>() {{
            put("dataFilePath", o.getDataFilePath());
            put("middleFolder", o.getMiddleFolder());
        }}).collect(Collectors.toList()));

        if (zipFile == null) {
            log.error(fileName + " 파일이 형성되지 않았습니다. 다시 시도 바랍니다.");
            return false;
        }

        MapVersionDTO mapVersionDTO = mapVersionMapper.findByMapIdAndVersion(mapDTO.getId(), newVersion);

        int res;
        String finalSavePath = Paths.get(savePath, fileName).toString();

        if (mapVersionDTO == null) {
            long id = mapVersionMapper.findNextSeqVal();
            res = mapVersionMapper.insert(new MapVersionDTO(id, mapDTO.getId(), Double.parseDouble(String.format("%.1f", newVersion)), finalSavePath.replace(dataPath, ""), zipFile.length(), new Date()));
            if (res > 0) {
                layers.forEach(o -> mapVersionMapper.insertRelate(id, o.getId()));
            } else return false;
        } else {
            mapVersionMapper.deleteRelateByVersionId(mapVersionDTO.getId());
            mapVersionDTO.setUploadDate(new Date());
            mapVersionDTO.setZipFilePath(finalSavePath.replace(dataPath, ""));
            mapVersionDTO.setZipFileSize(zipFile.length());
            res = mapVersionMapper.update(mapVersionDTO);
            if (res > 0) {
                layers.forEach(o -> mapVersionMapper.insertRelate(mapVersionDTO.getId(), o.getId()));
            } else return false;
        }

        if(!FileSystemUtil.isWindowOS()){
            // MAP VERSION 과정이 끝나면 모든 권한을 755 로 설정.
            try {
                FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(new File(dataPath + Constants.MAP_VERSION_FILE_PATH));
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * 변경되는 레이어들에 대한 버전 관리를 진행한다.
     * @param prevLayers List Of Layers, newLayers List Of Layers
     */
    @Transactional
    public void setMapLayerVersionManage(MapDTO map, List<LayerDTO> prevLayers, List<LayerDTO> nextLayers){
        // 데이터 변동 시, 이전에 설정했던 레이어들은 버전 변경에 배제를 한다. 변화량이 있는 데이터에 한해 계산해야 한다.
        Set<Long> prevIds = prevLayers.stream().map(LayerDTO::getId).collect(Collectors.toSet());
        Set<Long> nextIds = nextLayers.stream()
                .filter(o1 -> prevLayers.stream().filter(o2 -> o2.getId().equals(o1.getId()) && o2.getVersion().equals(o1.getVersion())).count() > 0)
                .map(LayerDTO::getId)
                .collect(Collectors.toSet());

        Set<Long> maintainIds = new HashSet<>(nextIds);
        maintainIds.retainAll(prevIds);

        Set<Double> layerVersions = loadVersionSetAtLayerList(prevLayers);
        Set<Double> mapVersions = mapVersionMapper.findByMapId(map.getId()).stream().map(MapVersionDTO::getVersion).collect(Collectors.toSet());
        MapVersionDTO current = mapVersionMapper.findByMapIdRecently(map.getId());
        for(LayerDTO layer : nextLayers){
            // 새로 추가하는 레이어 중에 같은 버전이 있는 경우 현재 버전의 0.1 를 높인다.
            // 단, 새로 추가하는 의미는 같은 ID 의 데이터인 경우는 이전 버전을 그대로 '유지' 하는 것에 중점을 둬야 한다.
            if(!maintainIds.contains(layer.getId())){
                // 이전 레이어 버전이 포함이 되어 있든, Map 의 버전이 포함되어 있든 현재 버전에 0.1 를 가차없이 올려버린다.
                if(layerVersions.contains(layer.getVersion()) || mapVersions.contains(layer.getVersion())) {
                    layer.setVersion(Double.parseDouble(String.format("%.1f", current.getVersion() + 0.1f)));
                    layerMapper.update(layer);
                    // 버전 업데이트 된 레이어 반영.
                    setLayerUpdateManage(layer);
                }
            }
        }
    }

    /**
     * MAP 파일을 삭제할 시, 혹은 중간에 버전 관리를 안 할 때, 버전 관리 폴더도 파기한다.
     * @param map MapDTO
     */
    @Transactional
    public void removeVersionWithMapData(MapDTO map){
        for(MapVersionDTO version : mapVersionMapper.findByMapId(map.getId())){
            mapVersionMapper.deleteRelateByVersionId(version.getId());
        }
        mapVersionMapper.deleteByMapId(map.getId());

        try {
            FileUtils.deleteDirectory(Paths.get(dataPath, Constants.MAP_VERSION_FILE_PATH, map.getName()).toFile());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * LAYER 정보가 바뀔 때 이를 보유한 MAP 데이터에 대한 버전 정보를 관리한다.
     * @param layer LayerDTO
     */
    @Transactional
    public void setLayerUpdateManage(LayerDTO layer){
        List<MapVersionDTO> versions = mapVersionMapper.findByLayerId(layer.getId());

        for(long mapId : versions.stream().map(MapVersionDTO::getMapId).collect(Collectors.toSet())){
            MapDTO map = mapMapper.findById(mapId);
            List<LayerDTO> prevLayers = layerMapper.findByMapId(mapId);
            List<LayerDTO> nextLayers = prevLayers.stream().map(o -> o.getId().equals(layer.getId()) ? layer : o).collect(Collectors.toList());
            setMapLayerVersionManage(map, prevLayers, nextLayers);
            saveMapVersionRecentlyStatus(map, nextLayers);
        }
    }

    /**
     * LAYER 정보가 삭제될 때 이를 보유한 MAP 데이터에 대한 버전 정보를 관리한다.
     * @param layer LayerDTO
     */
    @Transactional
    public void setLayerRemoveManage(LayerDTO layer){
        List<MapVersionDTO> versions = mapVersionMapper.findByLayerId(layer.getId());
        mapVersionMapper.deleteRelateByLayerId(layer.getId());
        for(MapVersionDTO version : versions){
            // 만약 버전 종속 레이어가 없는 경우에는 맵 버전도 삭제. 아닌 경우에는 삭제 뒤 버전 재조정 작업을 진행.
            if(mapVersionMapper.countLayersByMapVersionId(version.getId()) == 0) {
                mapVersionMapper.deleteById(version.getId());
                try {
                    FileSystemUtil.deleteFile(dataPath + version.getZipFilePath());
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            } else {
                MapDTO map = mapMapper.findById(version.getMapId());
                List<LayerDTO> prevLayers = layerMapper.findByMapId(version.getMapId());
                List<LayerDTO> nextLayers = prevLayers.stream().filter(o -> !o.getId().equals(layer.getId())).collect(Collectors.toList());
                saveMapVersionRecentlyStatus(map, nextLayers);
            }
        }
    }
}
