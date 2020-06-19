package com.jiin.admin.website.view.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.dto.MapLayerRelationDTO;
import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapLayerRelationMapper;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.MapVersionMapper;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.view.component.MapVersionManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
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
    private MapLayerRelationMapper mapLayerRelationMapper;

    @Resource
    private MapVersionMapper mapVersionMapper;

    @Resource
    private MapVersionManagement mapVersionManagement;

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
        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(relations, new TypeReference<LinkedList<Map<String, Object>>>(){});
        TreeMap<Integer, Long> layerMap = new TreeMap<>();
        for(Map<String, Object> orderLayer : orderLayerList){
            long layerId = Long.parseLong(String.valueOf(orderLayer.get("layerId")));
            int layerOrder = Integer.parseInt(String.valueOf(orderLayer.get("order")));
            layerMap.put(layerOrder, layerId);
        }

        List<LayerDTO> layers = new ArrayList<>();
        for(int order : layerMap.keySet()){
            long id = layerMap.getOrDefault(order, -1L);
            if(id != -1L) {
                LayerDTO layer = layerMapper.findById(id);
                if(layer != null) layers.add(layer);
            }
        }
        return layers;
    }


    /**
     * MAP 데이터 중 추가 및 삭제 시 공통으로 설정할 수 있는 로직 정리
     * @param mapDTO MapDTO
     */
    private void setCommonProperties(MapDTO mapDTO){
        // TODO : 굳이 setter 안 쓰고, Builder 를 사용해서 한 번에 실행하는 방법이 있을까? - Builder 패턴 알아볼 것
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mapDTO.setRegistorId(loginUser);
        mapDTO.setRegistorName(loginUser);
        mapDTO.setRegistTime(new Date());
    }

    /**
     * MAP 데이터와 LAYER 데이터와의 연동 관계 생성
     * @param mapId long, relations String JSON
     */
    private boolean createRelationByMapIdAndRelationJSON(long mapId, String relations) throws JsonProcessingException {
        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(relations, new TypeReference<LinkedList<Map<String, Object>>>(){});
        for (Map<String, Object> orderLayer : orderLayerList) {
            long layerId = Long.parseLong(String.valueOf(orderLayer.get("layerId")));
            int layerOrder = Integer.parseInt(String.valueOf(orderLayer.get("order")));
            MapLayerRelationDTO relation = new MapLayerRelationDTO(0L, mapId, layerId, layerOrder);
            if(mapLayerRelationMapper.insert(relation) < 1) return false;
        }
        return true;
    }

    /**
     * MAP 데이터와 LAYER 데이터와의 연동 관계 삭제
     * @param mapId long
     */
    private boolean removeRelationByMapId(long mapId){
        return mapLayerRelationMapper.deleteByMapId(mapId) > 0;
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
        for(MapDTO map : data){
            MapVersionDTO version = mapVersionMapper.findByMapIdRecently(map.getId());
            if(version != null) {
                versionMap.put(map.getId(), version.getVersion());
            }
        }
        return new HashMap<String, Object>(){{
            put("data", data);
            put("count", mapMapper.countByPageModel(mapPageModel));
            put("versionMap", versionMap);
        }};
    }

    @Override
    public Map<String, Object> loadVersionInfoListById(long id) {
        return new HashMap<String, Object>(){{
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
        if(mapMapper.findByName(mapDTO.getName()) != null) return false;

        long id = mapMapper.findNextSeqVal();
        String mapFilePath = String.format("%s%s/%s%s", dataPath, Constants.MAP_FILE_PATH, mapDTO.getName(), Constants.MAP_SUFFIX);
        List<LayerDTO> layers = loadLayersByRelationJSON(relations);

        mapDTO.setId(id);
        mapDTO.setMapFilePath(mapFilePath.replaceAll(dataPath, ""));
        mapDTO.setDefault(false);

        setCommonProperties(mapDTO);

        if(mapMapper.insert(mapDTO) > 0){
            // MAP - LAYER 관계 최초 생성
            createRelationByMapIdAndRelationJSON(id, relations);

            // *.map 파일 생성
            try {
                // 1단계. abc.map 파일 생성
                String fileContext = MapServerUtil.fetchMapFileContextWithDTO(defaultMap, dataPath, mapDTO, loadLayersByRelationJSON(relations));
                FileSystemUtil.createAtFile(mapFilePath, fileContext);

                // 2단계. Version 관리 (선택)
                if(versionCheck) {
                    mapVersionManagement.saveMapVersionRecentlyStatus(mapDTO, new ArrayList<>(), layers);
                }
            } catch (IOException e){
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

        if(selected == null) return false;

        mapDTO.setMapFilePath(selected.getMapFilePath()); // 이름은 변동할 수 없으니 현재까지 저장된 디렉토리를 그대로 유지.

        setCommonProperties(mapDTO);

        if(mapMapper.update(mapDTO) > 0) {
            // MAP - LAYER Relation 설정
            removeRelationByMapId(mapDTO.getId());
            createRelationByMapIdAndRelationJSON(mapDTO.getId(), relations);

            // *.map 파일 생성
            try {
                // 1단계. abc.map 파일 생성
                String mapFilePath = String.format("%s%s", dataPath, selected.getMapFilePath());
                String fileContext = MapServerUtil.fetchMapFileContextWithDTO(defaultMap, dataPath, mapDTO, layers);
                FileSystemUtil.createAtFile(mapFilePath, fileContext);

                if(versionCheck){
                    mapVersionManagement.setMapLayerListChangeManage(mapDTO, prevLayers, layers);
                } else {
                    mapVersionManagement.removeVersionWithMapData(selected);
                }

            } catch (IOException e){
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
    public boolean removeData(long id) {
        MapDTO selected = mapMapper.findById(id);
        if(selected == null) return false;
        removeRelationByMapId(id); // 현재 MAP, LAYER 종속 관계 삭제
        mapVersionManagement.removeVersionWithMapData(selected); // MAP 버전 폴더 및 DB 삭제
        if(mapMapper.deleteById(id) > 0) {
            try {
                String mapFilePath = String.format("%s%s", dataPath, selected.getMapFilePath());
                FileSystemUtil.deleteFile(mapFilePath);
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
