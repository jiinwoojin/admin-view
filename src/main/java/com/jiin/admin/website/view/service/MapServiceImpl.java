package com.jiin.admin.website.view.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.dto.*;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.GdalUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.view.component.CascadeRelativeComponent;
import com.jiin.admin.website.view.component.MapVersionManagement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class MapServiceImpl implements MapService {
    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map.map")
    private File defaultMap;

    @Resource
    private MapMapper mapMapper;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyCacheSourceRelationMapper proxyCacheSourceRelationMapper;

    @Resource
    private MapLayerRelationMapper mapLayerRelationMapper;

    @Resource
    private MapVersionMapper mapVersionMapper;

    @Resource
    private MapVersionManagement mapVersionManagement;

    @Resource
    private CascadeRelativeComponent cascadeRelativeComponent;

    private static final List<OptionModel> sbOptions = Arrays.asList(
        new OptionModel("-- 검색 키워드 선택 --", 0),
        new OptionModel("MAP 이름", 1),
        new OptionModel("MAP 등록자", 2),
        new OptionModel("MAP 좌표 체계", 3)
    );

    private static final List<OptionModel> obOptions = Arrays.asList(
        new OptionModel("-- 정렬 방식 선택 --", 0),
        new OptionModel("ID 순서 정렬", 1),
        new OptionModel("이름 순서 정렬", 2),
        new OptionModel("등록 기간 역순 정렬", 3)
    );

    /**
     * 연동 관계 데이터 중 레이어 순서에 맞춰 반환
     * @param relations String JSON
     */
    private List<LayerDTO> loadLayersByRelationJSON(String relations) throws JsonProcessingException {
        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(relations, new TypeReference<LinkedList<Map<String, Object>>>() {});
        TreeMap<Integer, Long> layerMap = new TreeMap<>();
        for (Map<String, Object> orderLayer : orderLayerList) {
            long layerId = Long.parseLong(String.valueOf(orderLayer.get("layerId")));
            int layerOrder = Integer.parseInt(String.valueOf(orderLayer.get("order")));
            layerMap.put(layerOrder, layerId);
        }

        List<LayerDTO> layers = new ArrayList<>();
        for (int order : layerMap.keySet()) {
            long id = layerMap.getOrDefault(order, -1L);
            if (id != -1L) {
                LayerDTO layer = layerMapper.findById(id);
                if (layer != null) layers.add(layer);
            }
        }
        return layers;
    }


    /**
     * MAP 데이터 중 추가 및 삭제 시 공통으로 설정할 수 있는 로직 정리
     * @param mapDTO MapDTO
     */
    private void setCommonProperties(MapDTO mapDTO) {
        // TODO : 굳이 setter 안 쓰고, Builder 를 사용해서 한 번에 실행하는 방법이 있을까? - Builder 패턴 알아볼 것
        AccountEntity user = (AccountEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mapDTO.setRegistorId(user.getUsername());
        mapDTO.setRegistorName(user.getName());
    }

    /**
     * MAP 데이터와 LAYER 데이터와의 연동 관계 생성
     * @param mapId long, relations String JSON
     */
    private boolean createRelationByMapIdAndRelationJSON(long mapId, String relations) throws JsonProcessingException {
        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(relations, new TypeReference<LinkedList<Map<String, Object>>>() {});
        for (Map<String, Object> orderLayer : orderLayerList) {
            long layerId = Long.parseLong(String.valueOf(orderLayer.get("layerId")));
            int layerOrder = Integer.parseInt(String.valueOf(orderLayer.get("order")));
            MapLayerRelationDTO relation = new MapLayerRelationDTO(0L, mapId, layerId, layerOrder);
            if (mapLayerRelationMapper.insert(relation) < 1) return false;
        }
        return true;
    }

    /**
     * MAP 데이터와 LAYER 데이터와의 연동 관계 삭제
     * @param mapId long
     */
    private boolean removeRelationByMapId(long mapId) {
        return mapLayerRelationMapper.deleteByMapId(mapId) > 0;
    }

    /**
     * MAP 데이터의 CACHE 데이터 용량을 가져온다.
     * @param name String
     */
    @Override
    public long loadMapCacheSeedCapacity(String name) {
        MapDTO map = mapMapper.findByName(name);
        if (map == null) {
            return 0L;
        } else {
            String srs = map.getProjection().toUpperCase();
            srs = srs.replace(":", "");

            String mapDir = Constants.MAP_FILE_PATH + "/" + String.format("%s%s", name, Constants.MAP_SUFFIX);
            List<ProxySourceDTO> sources = proxySourceMapper.findByRequestMapEndsWith(mapDir);
            Set<Long> cacheIds = new HashSet<>();
            for (ProxySourceDTO source : sources) {
                cacheIds.addAll(proxyCacheSourceRelationMapper.findCacheIdBySourceId(source.getId()));
            }

            long capacity = 0L;
            for (Long id : cacheIds) {
                ProxyCacheDTO cache = proxyCacheMapper.findById(id);
                String folder = dataPath + Constants.PROXY_CACHE_DIRECTORY + "/" + String.format("%s_%s", cache.getName(), srs);
                File file = new File(folder);
                if (file.exists()) {
                    capacity += FileUtils.sizeOfDirectory(file);
                }
            }

            return capacity;
        }
    }

    /**
     * REST API 에서 보여질 MAP 선택 기능
     * @param
     */
    @Override
    public List<MapDTO> loadMapDataList() {
        return mapMapper.findAll();
    }

    /**
     * MAP 검색 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadSearchByOptionList() {
        return sbOptions;
    }

    /**
     * MAP 정렬 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadOrderByOptionList() {
        return obOptions;
    }

    /**
     * MAP 데이터 목록 조회 with Pagination Model
     * @param mapPageModel MapPageModel
     */
    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(MapPageModel mapPageModel) {
        List<MapDTO> data = mapMapper.findByPageModel(mapPageModel);
        Map<Long, Double> versionMap = new HashMap<>();
        for (MapDTO map : data) {
            MapVersionDTO version = mapVersionMapper.findByMapIdRecently(map.getId());
            if (version != null) {
                versionMap.put(map.getId(), version.getVersion());
            }
        }
        return new HashMap<String, Object>() {{
            put("data", data);
            put("count", mapMapper.countByPageModel(mapPageModel));
            put("versionMap", versionMap);
        }};
    }

    @Override
    public Map<String, Object> loadVersionInfoListById(long id) {
        return new HashMap<String, Object>() {{
            put("list", mapVersionMapper.findByMapId(id));
        }};
    }

    /**
     * MAP 데이터 단일 조회
     * @param id long
     */
    @Override
    public MapDTO loadDataById(long id) {
        return mapMapper.findById(id);
    }

    /**
     * MAP 데이터 추가
     * @param mapDTO MapDTO, relations JSON String
     */
    @Override
    @Transactional
    public boolean createData(MapDTO mapDTO, String relations, boolean versionCheck) throws JsonProcessingException {
        if (mapMapper.findByName(mapDTO.getName()) != null) return false;

        long id = mapMapper.findNextSeqVal();
        String mapFilePath = String.format("%s%s/%s%s", dataPath, Constants.MAP_FILE_PATH, mapDTO.getName(), Constants.MAP_SUFFIX);
        List<LayerDTO> layers = loadLayersByRelationJSON(relations);

        mapDTO.setId(id);
        mapDTO.setMapFilePath(mapFilePath.replaceAll(dataPath, ""));
        mapDTO.setDefault(false);
        mapDTO.setRegistTime(new Date());

        setCommonProperties(mapDTO);

        if (versionCheck && mapVersionManagement.checkVrt(layers) == 0) {
            mapDTO.setVrtFilePath(Paths.get(Constants.VRT_FILE_PATH, String.format("%s%s", mapDTO.getName(), Constants.VRT_SUFFIX)).toString());
        }

        if (mapMapper.insert(mapDTO) > 0) {
            // MAP - LAYER 관계 최초 생성
            createRelationByMapIdAndRelationJSON(id, relations);

            // *.map 파일 생성
            try {
                // 1단계. abc.map 파일 생성
                String fileContext = MapServerUtil.fetchMapFileContextWithDTO(defaultMap, dataPath, mapDTO, loadLayersByRelationJSON(relations));
                FileSystemUtil.createAtFile(mapFilePath, fileContext);

                // 2단계. Version 관리 (선택)
                if (versionCheck) {
                    if (!StringUtils.isBlank(mapDTO.getVrtFilePath())) {
                        // vrt 생성
                        GdalUtil.createVrt(dataPath, mapDTO, layers);   // TODO return 값 받아서 처리 해야함
                    }

                    // Version set 생성
                    mapVersionManagement.saveMapVersionRecentlyStatus(mapDTO, new ArrayList<>(), layers);
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                return false;
            }

            log.info(mapDTO.getName() + " MAP 파일 생성 성공했습니다.");
            return true;
        }

        return false;
    }

    /**
     * MAP 데이터 수정
     * @param mapDTO MapDTO, relations JSON String
     */
    @Override
    @Transactional
    public boolean setData(MapDTO mapDTO, String relations, boolean versionCheck) throws JsonProcessingException {
        MapDTO selected = mapMapper.findByName(mapDTO.getName());

        List<LayerDTO> prevLayers = layerMapper.findByMapId(selected.getId());
        List<LayerDTO> layers = loadLayersByRelationJSON(relations);

        if (selected == null) {
            return false;
        }
        if (selected.isDefault()) {
            return false;
        }

        mapDTO.setRegistTime(selected.getRegistTime()); // 기존 등록 시간을 저장한다.
        mapDTO.setMapFilePath(selected.getMapFilePath()); // 이름은 변동할 수 없으니 현재까지 저장된 디렉토리를 그대로 유지.
        mapDTO.setUpdateTime(new Date());

        setCommonProperties(mapDTO);

        if (mapMapper.update(mapDTO) > 0) {
            // MAP - LAYER Relation 설정
            removeRelationByMapId(mapDTO.getId());
            createRelationByMapIdAndRelationJSON(mapDTO.getId(), relations);

            // *.map 파일 생성
            try {
                // 1단계. abc.map 파일 생성
                String mapFilePath = String.format("%s%s", dataPath, selected.getMapFilePath());
                String fileContext = MapServerUtil.fetchMapFileContextWithDTO(defaultMap, dataPath, mapDTO, layers);
                FileSystemUtil.createAtFile(mapFilePath, fileContext);

                if (versionCheck) {
                    if (mapVersionManagement.checkVrt(layers) == 0) {
                        // vrt 생성
                        GdalUtil.createVrt(dataPath, mapDTO, layers);   // TODO return 값 받아서 처리 해야함
                    } else {
                        // TODO vrt 파일이 있을경우 삭제 해야함
                        if (!StringUtils.isBlank(mapDTO.getVrtFilePath())) {
                            mapDTO.setVrtFilePath(null);
                            mapMapper.update(mapDTO);
                        }
                    }

                    mapVersionManagement.setMapLayerListChangeManage(mapDTO, prevLayers, layers);
                } else {
                    mapVersionManagement.removeVersionWithMapData(selected);
                }

            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                return false;
            }

            log.info(mapDTO.getName() + " MAP 파일 수정 성공했습니다.");
            return true;
        }

        return false;
    }

    /**
     * MAP 데이터 삭제
     * @param id long
     */
    @Override
    @Transactional
    public boolean removeData(long id) {
        MapDTO selected = mapMapper.findById(id);
        List<LayerDTO> layers = layerMapper.findByMapId(id);
        if (selected == null) {
            return false;
        }
        if (selected.isDefault()) {
            return false;
        }

        // CASCADE 삭제
        List<ProxySourceDTO> cascadeProxySources = cascadeRelativeComponent.loadMapRemoveAfterOrphanProxySources(id);
        if(cascadeProxySources.size() > 0) {
            cascadeRelativeComponent.removeMapFileWithOrphanCheck(id);
            return true;
        }

        // MAP 을 삭제하면, LAYER 버전을 전부 1.0 으로 초기화. (단 버전을 관리할 때.)
        List<MapVersionDTO> mapVersions = mapVersionMapper.findByLayerId(id);
        if (!mapVersions.isEmpty()) {
            layers.forEach(o -> {
                o.setVersion(Double.parseDouble(String.format("%.1f", Constants.DEFAULT_LAYER_VERSION)));
                layerMapper.update(o);
            });
        }

        removeRelationByMapId(id); // 현재 MAP, LAYER 종속 관계 삭제
        if (mapVersionMapper.findByMapId(id) != null) {
            mapVersionManagement.removeVersionWithMapData(selected); // MAP 버전 폴더 및 DB 삭제
        }
        if (mapMapper.deleteById(id) > 0) {
            try {
                if (!StringUtils.isBlank(selected.getMapFilePath())) {
                    String mapFilePath = String.format("%s%s", dataPath, selected.getMapFilePath());
                    FileSystemUtil.deleteFile(mapFilePath);
                }

                if (!StringUtils.isBlank(selected.getVrtFilePath())) {
                    String vrtFilePath = String.format("%s%s", dataPath, selected.getVrtFilePath());
                    FileSystemUtil.deleteFile(vrtFilePath);
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                return false;
            }
            log.info(selected.getName() + " MAP 파일 삭제 성공했습니다.");
            return true;
        }
        return false;
    }
}
