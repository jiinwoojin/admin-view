package com.jiin.admin.website.view.component;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.MapVersionMapper;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.GdalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.jalokim.utils.string.StringUtils;

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
     * @param map MapDTO, baseVersion double
     */
    private void cleanCurrentVersionData(MapDTO map, double baseVersion) {
        List<MapVersionDTO> versions = mapVersionMapper.findByMapId(map.getId());
        for (MapVersionDTO version : versions) {
            if (version.getVersion() > baseVersion) {
                mapVersionMapper.deleteRelateByVersionId(version.getId());
                mapVersionMapper.deleteById(version.getId());
                String filePath = dataPath + version.getZipFilePath();
                try {
                    if(!StringUtils.isBlank(filePath)) {
                        FileSystemUtil.deleteFile(filePath);
                    }
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            }
        }
    }

    /**
     * 새로 저장할 LAYER 중에 순수 새롭게 저장될 레이어만 보낸다.
     * @param prevLayers LayerDTO, nextLayers LayerDTO
     */
    private List<LayerDTO> loadPureNewLayersInNextLayers(List<LayerDTO> prevLayers, List<LayerDTO> nextLayers) {
        Set<Long> prevIds = prevLayers.stream().map(o -> o.getId()).collect(Collectors.toSet());
        Set<Long> nextIds = nextLayers.stream()
                .filter(o1 -> prevLayers.stream().filter(o2 -> o2.getId().equals(o1.getId()) && o2.getVersion().equals(o1.getVersion())).count() > 0)
                .map(LayerDTO::getId)
                .collect(Collectors.toSet());

        Set<Long> maintainIds = new HashSet<>(nextIds);
        maintainIds.retainAll(prevIds);

        return nextLayers.stream().filter(o -> !maintainIds.contains(o.getId())).collect(Collectors.toList());
    }

    /**
     * 레이어들 중에서 가장 큰 버전을 가져온다.
     * @param layers List Of Layers
     */
    private Double loadMaximumVersionAtLayerList(List<LayerDTO> layers) {
        return Collections.max(layers.stream().filter(o -> o.getVersion() != null).map(LayerDTO::getVersion).collect(Collectors.toSet()));
    }

    /**
     * 레이어들 중에서 버전들의 집합체를 불러온다.
     * @param layers List Of Layers
     */
    private Set<Double> loadVersionSetAtLayerList(List<LayerDTO> layers) {
        return layers.stream().filter(o -> o.getVersion() != null).map(LayerDTO::getVersion).collect(Collectors.toSet());
    }

    /**
     * MAP 데이터를 추가 및 수정한 이후, 버전 관리 기능을 실시한다.
     * @param mapDTO MapDTO, layers List of Layers
     */
    @Transactional
    public void saveMapVersionRecentlyStatus(MapDTO mapDTO, List<LayerDTO> prevLayers, List<LayerDTO> nextLayers) {
        double newVersion = loadMaximumVersionAtLayerList(nextLayers);

        // 종속된 LAYER 중 가장 신 버전이 삭제 되어 버전이 낮아지는 경우에 실행되는 메소드.
        MapVersionDTO current = mapVersionMapper.findByMapIdRecently(mapDTO.getId());
        if (current != null) {
            if (newVersion < current.getVersion()) {
                cleanCurrentVersionData(mapDTO, newVersion);
            }
        }

        // 아래 문장은 1.9 -> 2.0, 2.9 -> 3.0 등으로 넘어갈 때 모든 버전 데이터를 삭제하기 위한 로직에서 활용하면 될 것이다...
        if (newVersion == Math.ceil(newVersion)) {
            log.info("새로운 버전이 정수이기 때문에 초기화 됩니다.");
        }

        List<LayerDTO> pureNewLayers = loadPureNewLayersInNextLayers(prevLayers, nextLayers);
        Map<Double, List<LayerDTO>> versionMap = new HashMap<>();
        for (LayerDTO layer : pureNewLayers) {
            List<LayerDTO> tmpLayers = versionMap.getOrDefault(layer.getVersion(), new ArrayList<>());
            tmpLayers.add(layer);
            versionMap.put(layer.getVersion(), tmpLayers);
        }

        for (Double version : versionMap.keySet()) {
            List<LayerDTO> tmpLayers = versionMap.getOrDefault(version, new ArrayList<>());
            String savePath = Paths.get(dataPath, Constants.MAP_VERSION_FILE_PATH, mapDTO.getName()).toString();
            String fileName = String.format("%s_V%.1f.zip", mapDTO.getName(), version);
            List<Map<String, String>> paths = tmpLayers.stream().map(o -> new HashMap<String, String>() {{
                put("dataFilePath", o.getDataFilePath());
                put("middleFolder", o.getMiddleFolder());
            }}).collect(Collectors.toList());

            // TODO 정리 필요
            // vrt 파일이 있을 경우 vrt 파일도 같이 만들어 준다
            if (mapDTO.getVrtFilePath() != null) {
                paths.add(new HashMap<String, String>() {
                    {
                        put("vrtFilePath", mapDTO.getVrtFilePath());
                        put("middleFolder", mapDTO.getName());
                    }
                });
            }

            File zipFile = FileSystemUtil.saveZipFileWithPaths(dataPath, savePath, fileName, paths);
            if (zipFile == null) {
                log.error(fileName + " 파일이 형성되지 않았습니다. 다시 시도 바랍니다.");
            }

            MapVersionDTO mapVersionDTO = mapVersionMapper.findByMapIdAndVersion(mapDTO.getId(), version);

            int res;
            String finalSavePath = Paths.get(savePath, fileName).toString();

            if (mapVersionDTO == null) {
                long id = mapVersionMapper.findNextSeqVal();
                res = mapVersionMapper.insert(new MapVersionDTO(id, mapDTO.getId(), Double.parseDouble(String.format("%.1f", version)), finalSavePath.replace("\\", "/").replace(dataPath, ""), zipFile.length(), new Date()));
                if (res > 0) {
                    tmpLayers.forEach(o -> mapVersionMapper.insertRelate(id, o.getId()));
                }
            } else {
                mapVersionMapper.deleteRelateByVersionId(mapVersionDTO.getId());
                mapVersionDTO.setUploadDate(new Date());
                mapVersionDTO.setZipFilePath(finalSavePath.replace("\\", "/").replace(dataPath, ""));
                mapVersionDTO.setZipFileSize(zipFile.length());
                res = mapVersionMapper.update(mapVersionDTO);
                if (res > 0) {
                    tmpLayers.forEach(o -> mapVersionMapper.insertRelate(mapVersionDTO.getId(), o.getId()));
                }
            }
        }

        if (!FileSystemUtil.isWindowOS()) {
            // MAP VERSION 과정이 끝나면 모든 권한을 755 로 설정.
            try {
                FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(new File(dataPath + Constants.MAP_VERSION_FILE_PATH + String.format("/%s", mapDTO.getName())));
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }
    }

    /**
     * 변경되는 레이어들에 대해 새로 부여할 버전을 계산한다.
     * @param prevLayers List Of Layers, newLayers List Of Layers
     */
    public double calculateMapVersionInNextLayers(MapDTO map, List<LayerDTO> prevLayers, List<LayerDTO> nextLayers) {
        MapVersionDTO current = mapVersionMapper.findByMapIdRecently(map.getId());
        List<LayerDTO> pureNewLayer = loadPureNewLayersInNextLayers(prevLayers, nextLayers);
        double newVersion = (current == null) ? Constants.DEFAULT_LAYER_VERSION : current.getVersion();
        double nextMaxVersion = Constants.DEFAULT_LAYER_VERSION;
        if (pureNewLayer.size() > 0) {
            nextMaxVersion = Collections.max(pureNewLayer.stream().map(o -> o.getVersion()).collect(Collectors.toSet()));
        }
        return Math.max(Double.parseDouble(String.format("%.1f", newVersion + Constants.ASCEND_LAYER_VERSION)), nextMaxVersion);
    }

    /**
     * MAP 파일을 삭제할 시, 혹은 중간에 버전 관리를 안 할 때, 버전 관리 폴더도 파기한다.
     * @param map MapDTO
     */
    @Transactional
    public void removeVersionWithMapData(MapDTO map) {
        for (MapVersionDTO version : mapVersionMapper.findByMapId(map.getId())) {
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
     * MAP 에서 LAYER 목록이 달라질 때 실행해서 버전 정보를 관리한다.
     * @param map MapDTO, prevLayers List of Layers, nextLayers List of Layers
     */
    @Transactional
    public void setMapLayerListChangeManage(MapDTO map, List<LayerDTO> prevLayers, List<LayerDTO> nextLayers) {
        double newVersion = calculateMapVersionInNextLayers(map, prevLayers, nextLayers);
        List<LayerDTO> pureNewLayers = loadPureNewLayersInNextLayers(prevLayers, nextLayers);
        List<LayerDTO> newLayers = new ArrayList<>(nextLayers);
        for (LayerDTO layer : pureNewLayers) {
            layer.setVersion(newVersion);
            layerMapper.update(layer);
            setLayerUpdateManage(layer);
            newLayers = newLayers.stream().map(o -> o.getId().equals(layer.getId()) ? layer : o).collect(Collectors.toList());
        }
        saveMapVersionRecentlyStatus(map, prevLayers, newLayers);
    }

    /**
     * LAYER 정보가 바뀔 때 이를 보유한 MAP 데이터에 대한 버전 정보를 관리한다.
     * @param layer LayerDTO
     */
    @Transactional
    public void setLayerUpdateManage(LayerDTO layer) {
        List<MapVersionDTO> versions = mapVersionMapper.findByLayerId(layer.getId());

        double maxVersion = Constants.DEFAULT_LAYER_VERSION;
        Map<Long, Object> layerMap = new HashMap<>();
        for (long mapId : versions.stream().map(MapVersionDTO::getMapId).collect(Collectors.toSet())) {
            MapDTO map = mapMapper.findById(mapId);
            List<LayerDTO> prevLayers = layerMapper.findByMapId(mapId);
            List<LayerDTO> nextLayers = prevLayers.stream().map(o -> o.getId().equals(layer.getId()) ? layer : o).collect(Collectors.toList());

            layerMap.put(mapId, new HashMap<String, List<LayerDTO>>() {{
                put("prevLayers", prevLayers);
                put("nextLayers", nextLayers);
            }});

            double newVersion = calculateMapVersionInNextLayers(map, prevLayers, nextLayers);
            maxVersion = Math.max(newVersion, maxVersion);
        }

        // 2단계. 큰 버전을 각 레이어에 설정한다.
        Set<Long> savedLayers = new HashSet<>();
        for (long mapId : layerMap.keySet()) {
            List<MapVersionDTO> mapVersions = mapVersionMapper.findByMapId(mapId);
            Map<String, List<LayerDTO>> listMap = (Map<String, List<LayerDTO>>) layerMap.get(mapId);
            List<LayerDTO> prevLayers = listMap.get("prevLayers");
            List<LayerDTO> nextLayers = listMap.get("nextLayers");
            List<LayerDTO> pureNewLayers = loadPureNewLayersInNextLayers(prevLayers, nextLayers);

            Set<Double> layerVersions = prevLayers.stream().map(LayerDTO::getVersion).collect(Collectors.toSet());
            Set<Double> mapUsedVersions = mapVersions.stream().map(MapVersionDTO::getVersion).collect(Collectors.toSet());
            for (LayerDTO newLayer : pureNewLayers) {
                if ((layerVersions.contains(newLayer.getVersion()) || mapUsedVersions.contains(newLayer.getVersion())) && !savedLayers.contains(newLayer.getId())) {
                    newLayer.setVersion(maxVersion);
                    layerMapper.update(newLayer);
                    savedLayers.add(newLayer.getId());

                    nextLayers = nextLayers.stream().map(o -> o.getId().equals(newLayer.getId()) ? newLayer : o).collect(Collectors.toList());
                    listMap.put("nextLayers", nextLayers);
                    layerMap.put(mapId, listMap);
                }
            }
        }

        // 3단계. 각 레이어 목록으로 버전 관리를 시작한다.
        for (long mapId : layerMap.keySet()) {
            MapDTO map = mapMapper.findById(mapId);

            if (map.getVrtFilePath() != null && !map.getVrtFilePath().equalsIgnoreCase("")) {
                // vrt 생성 TODO 정리 필요
                List<LayerDTO> layers = layerMapper.findByMapId(mapId);

                if (checkVrt(layers) == 0) {
                    GdalUtil.createVrt(dataPath, map, layers);
                } else {
                    // TODO vrt 파일이 있을경우 삭제 해야함
                    map.setVrtFilePath(null);
                    mapMapper.update(map);      // vrt file path 정보가 있을 경우 제거
                }
            }


            Map<String, List<LayerDTO>> listMap = (Map<String, List<LayerDTO>>) layerMap.get(mapId);
            List<LayerDTO> prevLayers = listMap.get("prevLayers");
            List<LayerDTO> nextLayers = listMap.get("nextLayers");
            saveMapVersionRecentlyStatus(map, prevLayers, nextLayers);
        }
    }

    /**
     * 해당 레이어에 CADRG 타입의 데이터가 있는지 확인
     * @param layers layer
     * @return int
     */
    public int checkVrt(List<LayerDTO> layers) {
        return (int) layers.stream().filter(l -> l.getType().equalsIgnoreCase("CADRG")).count();
    }

    /**
     * LAYER 정보가 삭제될 때 이를 보유한 MAP 데이터에 대한 버전 정보를 관리한다.
     * @param layer LayerDTO
     */
    @Transactional
    public void setLayerRemoveManage(LayerDTO layer) {
        List<MapVersionDTO> versions = mapVersionMapper.findByLayerId(layer.getId());
        mapVersionMapper.deleteRelateByLayerId(layer.getId());
        for (MapVersionDTO version : versions) {
            // 만약 버전 종속 레이어가 없는 경우에는 맵 버전도 삭제. 아닌 경우에는 삭제 뒤 버전 재조정 작업을 진행.
            if (mapVersionMapper.countLayersByMapVersionId(version.getId()) == 0) {
                mapVersionMapper.deleteById(version.getId());
                try {
                    if(!StringUtils.isBlank(version.getZipFilePath())) {
                        FileSystemUtil.deleteFile(dataPath + version.getZipFilePath());
                    }
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            } else {
                MapDTO map = mapMapper.findById(version.getMapId());
                List<LayerDTO> prevLayers = layerMapper.findByMapId(version.getMapId());
                List<LayerDTO> nextLayers = prevLayers.stream().filter(o -> !o.getId().equals(layer.getId())).collect(Collectors.toList());
                saveMapVersionRecentlyStatus(map, prevLayers, nextLayers);
            }
        }
    }
}
