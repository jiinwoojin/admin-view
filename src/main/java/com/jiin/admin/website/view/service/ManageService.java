package com.jiin.admin.website.view.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.entity.MapLayerRelationEntity;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.view.mapper.ManageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ManageService {
    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-layer.lay")
    File defaultLayer;

    @Value("classpath:data/default-map.map")
    File defaultMap;

    @Resource
    private ManageMapper mapper;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * MapEntity 내용 보충
     * @param map MapEntity, layerList String
     * @exception JsonProcessingException Exception
     */
    private void mapEntitySupplement(MapEntity map, String layerList) throws JsonProcessingException {
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.setDefault(false);
        map.setRegistorId(loginUser);
        map.setRegistorName(loginUser);
        map.setRegistTime(new Date());

        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(layerList, new TypeReference<LinkedList<Map<String, Object>>>(){});
        for (java.util.Map<String, Object> orderLayer : orderLayerList) {
            LayerEntity layer = entityManager.find(LayerEntity.class, Long.parseLong(String.valueOf(orderLayer.get("layerId"))));
            MapLayerRelationEntity mapLayerRelation = new MapLayerRelationEntity();
            mapLayerRelation.setMap(map);
            mapLayerRelation.setLayer(layer);
            mapLayerRelation.setLayerOrder(Integer.parseInt(String.valueOf(orderLayer.get("order"))));
            map.getMapLayerRelations().add(mapLayerRelation);
        }

        String mapFilePath = dataPath + Constants.MAP_FILE_PATH + "/" + map.getName() + Constants.MAP_SUFFIX;
        map.setMapFilePath(Objects.requireNonNull(mapFilePath).replaceAll(dataPath, ""));
    }

    /**
     * abc.map 파일 생성
     * @param map MapEntity, layerList String
     * @exception IOException Exception
     */
    private void writeMapFileContext(MapEntity map, String layerList) throws IOException {
        StringBuilder layerBuilder = new StringBuilder();
        List<Map<String, Object>> orderLayerList = new ObjectMapper().readValue(layerList, new TypeReference<LinkedList<Map<String, Object>>>(){});
        for (java.util.Map<String, Object> orderLayer : orderLayerList) {
            LayerEntity layer = entityManager.find(LayerEntity.class, Long.parseLong(String.valueOf(orderLayer.get("layerId"))));
            if(layer != null) {
                layerBuilder.append("  INCLUDE \"./layer/").append(layer.getName()).append(Constants.LAY_SUFFIX).append("\"");
                layerBuilder.append(System.lineSeparator());
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(defaultMap))) {
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
                    // 레이어 등록
                    // INCLUDE ./layer/레이어명.lay
                    line = layerBuilder.toString();
                }

                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // map 파일 생성
        String mapFilePath = dataPath + Constants.MAP_FILE_PATH + "/" + map.getName() + Constants.MAP_SUFFIX;
        System.out.println(mapFilePath);
        if (FileUtils.getFile(mapFilePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(mapFilePath));
        }

        FileSystemUtil.createAtFile(mapFilePath, stringBuilder.toString());
    }

    /* MAP 관련 메소드 (MapManageService 분류 예상) */

    /**
     * MAP 데이터 일반 목록
     */
    public List<MapEntity> getSourceList() {
        return mapper.getSourceList();
    }

    /**
     * MAP 검색 조건 옵션 목록
     */
    public List<OptionModel> mapSearchByOptions(){
        return Arrays.asList(
            new OptionModel("-- 검색 키워드 선택 --", 0),
            new OptionModel("MAP 이름", 1),
            new OptionModel("MAP 등록자", 2),
            new OptionModel("MAP 좌표 체계", 3)
        );
    }

    /**
     * MAP 정렬 조건 옵션 목록
     */
    public List<OptionModel> mapOrderByOptions(){
        return Arrays.asList(
            new OptionModel("-- 정렬 방식 선택 --", 0),
            new OptionModel("ID 순서 정렬", 1),
            new OptionModel("이름 순서 정렬", 2),
            new OptionModel("등록 기간 역순 정렬", 3)
        );
    }

    /**
     * MAP 정렬 조건 옵션 목록
     */
    public MapEntity findMapEntityById(Long id){
        return entityManager.find(MapEntity.class, id);
    }

    /**
     * MAP 데이터 목록 조회 with Pagination Model
     * @param mapPageModel MapSearchModel
     */
    public Map<String, Object> getMapListByPaginationModel(MapPageModel mapPageModel) {
        final Sort[] sorts = {
            Sort.by("id").descending(),
            Sort.by("id"),
            Sort.by("name"),
            Sort.by("regist_time").descending()
        };

        if(mapPageModel.getSb() == 3)
            mapPageModel.setSt(mapPageModel.getSt() != null ? mapPageModel.getSt().toLowerCase() : "");

        List<MapEntity> sbRes = mapper.findMapEntitiesByPaginationModel(mapPageModel);
        Pageable pageable = PageRequest.of(mapPageModel.getPg() - 1, mapPageModel.getSz(), sorts[mapPageModel.getOb()]);

        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();
        long pageEnd = (pageOffset + pageSize) > sbRes.size() ? sbRes.size() : pageOffset + pageSize;

        Page<MapEntity> page = new PageImpl<>(sbRes.subList((int) pageOffset, (int) pageEnd), pageable, sbRes.size());

        Map<String, Object> map = new HashMap<>();
        map.put("count", page.getTotalElements());
        map.put("data", page.getContent());

        return map;
    }

    /**
     * MAP 데이터 추가 (MapServer abc.map 파일)
     * @param map MapEntity, layerList String
     * @throws IOException Exception
     */
    @Transactional
    public boolean addMap(MapEntity map, String layerList) throws IOException {
        MapEntity entity = mapper.findMapEntityByName(map.getName());
        if(entity != null) {
            return false;
        } else {
            this.mapEntitySupplement(map, layerList);
            entityManager.persist(map);
            this.writeMapFileContext(map, layerList);
            return true;
        }
    }

    /**
     * MAP 데이터 수정 (MapServer abc.map 파일)
     * @param map MapEntity, layerList String
     * @throws IOException Exception
     */
    @Transactional
    public boolean updateMap(MapEntity map, String layerList) throws IOException {
        MapEntity entity = mapper.findMapEntityByName(map.getName());
        if(entity == null) {
            return false;
        } else {
            mapper.deleteLayerRelationsByMapId(map.getId());
            System.out.println(layerList);
            this.mapEntitySupplement(map, layerList);
            entityManager.merge(map);
            this.writeMapFileContext(map, layerList);
            return true;
        }
    }

    /**
     * MAP 데이터 삭제 (MapServer abc.map 파일)
     * @param mapId Long
     */
    @Transactional
    public boolean delMap(Long mapId) {
        MapEntity map = entityManager.find(MapEntity.class, mapId);
        String mapFilePath = dataPath + map.getMapFilePath();

        // lay 파일 삭제
        try {
            FileSystemUtil.deleteFile(mapFilePath);
        } catch (IOException e) {
            log.error(map.getName() + " LAY 파일 삭제 실패했습니다.");
        }

        entityManager.remove(map);
        return true;
    }

    /* LAYER 관련 함수 (LayerManageService 분류 예상) */

    /**
     * LayerEntity 내용 보충
     * @param name          이름
     * @param description   설명
     * @param projection    투영법
     * @param middle_folder 중간 폴더 구조
     * @param type          종류 (raster / vector)
     * @param data_file     지도 파일
     * @return              성공 여부
     * @exception JsonProcessingException Exception
     */
    private LayerEntity layerEntitySupplement(String method, Long id, String name, String description, String projection, String middle_folder, String type, MultipartFile data_file) throws IOException {
        String filePath = null;
        File dataFile;
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LayerEntity layer = entityManager.find(LayerEntity.class, id);
        if(layer == null) {
            layer = new LayerEntity();
        }

        if(data_file != null && data_file.getSize() > 0){
            if(method.equalsIgnoreCase("UPDATE")){
                String dataFilePath = dataPath + layer.getDataFilePath();
                try {
                    FileSystemUtil.deleteFile(dataFilePath);
                } catch (IOException e) {
                    log.error(layer.getName() + " DATA 파일 삭제 실패했습니다.");
                }
            }

            String dirPath = dataPath + Constants.DATA_PATH + "/" + middle_folder;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                if (dir.mkdirs() && !System.getProperty("os.name").toLowerCase().contains("win")) {
                    FileSystemUtil.setFileDefaultPermissions(dir.toPath());
                }
            }

            filePath = dirPath + "/" + data_file.getOriginalFilename();
            dataFile = new File(filePath);
            data_file.transferTo(dataFile);

            if(!System.getProperty("os.name").toLowerCase().contains("win")) FileSystemUtil.setFileDefaultPermissions(dataFile.toPath());
        }

        layer.setDefault(false);
        layer.setName(name);
        layer.setDescription(description);
        layer.setProjection(projection);
        layer.setType(type.toUpperCase());
        layer.setMiddleFolder(middle_folder);
        layer.setDataFilePath(Objects.requireNonNull(filePath).replaceAll(dataPath, ""));
        layer.setRegistorId(loginUser);
        layer.setRegistorName(loginUser);
        layer.setRegistTime(new Date());

        String layFilePath = dataPath + Constants.LAY_FILE_PATH + "/" + layer.getName() + Constants.LAY_SUFFIX;
        layer.setLayerFilePath(Objects.requireNonNull(layFilePath).replaceAll(dataPath, ""));

        return layer;
    }

    /**
     * abc.lay 파일 생성
     * @param layer LayerEntity
     * @exception IOException Exception
     */
    private void writeLayFileContext(LayerEntity layer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(defaultLayer))) {
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

                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // lay 파일 생성
        String layFilePath = dataPath + Constants.LAY_FILE_PATH + "/" + layer.getName() + Constants.LAY_SUFFIX;
        FileSystemUtil.createAtFile(layFilePath, stringBuilder.toString());
    }

    /**
     * LAYER 데이터 일반 목록
     */
    public List<LayerEntity> getLayerList() {
        return mapper.getLayerList();
    }

    /**
     * MAP 레이어가 가진 LAYER 데이터 목록 조회
     * @param mapId long
     */
    public List<LayerEntity> findLayerEntitiesByMapId(long mapId){
        return mapper.findLayerEntitiesByMapId(mapId);
    }

    /**
     * LAYER 검색 조건 옵션 목록
     */
    public List<OptionModel> layerSearchByOptions(){
        return Arrays.asList(
            new OptionModel("-- 검색 키워드 선택 --", 0),
            new OptionModel("레이어 이름", 1),
            new OptionModel("레이어 등록자", 2),
            new OptionModel("레이어 좌표 체계", 3)
        );
    }

    /**
     * LAYER 정렬 조건 옵션 목록
     */
    public List<OptionModel> layerOrderByOptions(){
        return Arrays.asList(
            new OptionModel("-- 정렬 방식 선택 --", 0),
            new OptionModel("ID 순서 정렬", 1),
            new OptionModel("이름 순서 정렬", 2),
            new OptionModel("등록 기간 역순 정렬", 3)
        );
    }

    /**
     * LAYER 데이터 목록 조회 with Pagination Model
     * @param layerPageModel LayerSearchModel
     */
    public Map<String, Object> getLayerListByPaginationModel(LayerPageModel layerPageModel) {
        final Sort[] sorts = {
                Sort.by("id").descending(),
                Sort.by("id"),
                Sort.by("name"),
                Sort.by("regist_time").descending(),
        };

        if(layerPageModel.getSb() == 3)
            layerPageModel.setSt(layerPageModel.getSt() != null ? layerPageModel.getSt().toLowerCase() : "");

        List<LayerEntity> sbRes = mapper.findLayerEntitiesByPaginationModel(layerPageModel);
        Pageable pageable = PageRequest.of(layerPageModel.getPg() - 1, layerPageModel.getSz(), sorts[layerPageModel.getOb()]);

        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();
        long pageEnd = (pageOffset + pageSize) > sbRes.size() ? sbRes.size() : pageOffset + pageSize;

        Page<LayerEntity> page = new PageImpl<>(sbRes.subList((int) pageOffset, (int) pageEnd), pageable, sbRes.size());

        Map<String, Object> map = new HashMap<>();
        map.put("count", page.getTotalElements());
        map.put("data", page.getContent());

        return map;
    }

    /**
     * Layer 등록
     * @param name          이름
     * @param description   설명
     * @param projection    투영법
     * @param middle_folder 중간 폴더 구조
     * @param type          종류 (raster / vector)
     * @param data_file     지도 파일
     * @return              성공 여부
     * @throws IOException  exception
     */
    @Transactional
    public boolean addLayer(String name, String description, String projection, String middle_folder, String type, MultipartFile data_file) throws IOException {
        log.info("Folder : " + middle_folder);
        log.info("Data Folder : " + dataPath + Constants.DATA_PATH + "/" + middle_folder);

        LayerEntity l = mapper.findLayerEntityByName(name);
        if(l == null){
            LayerEntity layer = this.layerEntitySupplement("INSERT", 0L, name, description, projection, middle_folder, type, data_file);
            entityManager.persist(layer);
            this.writeLayFileContext(layer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Layer 등록
     * @param name          이름
     * @param description   설명
     * @param projection    투영법
     * @param middle_folder 중간 폴더 구조
     * @param type          종류 (raster / vector)
     * @param data_file     지도 파일
     * @return              성공 여부
     * @throws IOException  exception
     */
    @Transactional
    public boolean updateLayer(long id, String name, String description, String projection, String middle_folder, String type, MultipartFile data_file) throws IOException {
        log.info("Folder : " + middle_folder);
        log.info("Data Folder : " + dataPath + Constants.DATA_PATH + "/" + middle_folder);

        LayerEntity l = mapper.findLayerEntityByName(name);
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(l != null){
            LayerEntity layer;
            if(!data_file.isEmpty()) {
                layer = this.layerEntitySupplement("UPDATE", id, name, description, projection, middle_folder, type, data_file);
            } else {
                layer = new LayerEntity();
                layer.setId(l.getId());
                layer.setName(name);
                layer.setDescription(description);
                layer.setProjection(projection);
                // 파일 수정 시, 새로운 파일을 업로드 안 하는 이상 중간 폴더 위치 입력을 막을 필요가 있음.
                // layer.setMiddleFolder(middle_folder);
                layer.setType(type);
                layer.setMiddleFolder(l.getMiddleFolder());
                layer.setDataFilePath(l.getDataFilePath());
                layer.setLayerFilePath(l.getLayerFilePath());
                layer.setRegistorId(loginUser);
                layer.setRegistorName(loginUser);
                layer.setRegistTime(new Date());
                layer.setDefault(false);
            }
            entityManager.merge(layer);
            this.writeLayFileContext(layer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * LAYER 데이터 삭제 (MapServer abc.lay 파일)
     * TODO 폴더에 파일이 없을 경우 폴더 삭제 로직 추가 해야함
     * @param layerId layerId Long
     */
    @Transactional
    public boolean delLayer(Long layerId) {
        LayerEntity layer = entityManager.find(LayerEntity.class, layerId);

        // DATA 파일 삭제
        String dataFilePath = dataPath + layer.getDataFilePath();
        String layFilePath = dataPath + layer.getLayerFilePath();
        try {
            FileSystemUtil.deleteFile(dataFilePath);
        } catch (IOException e) {
            log.error(layer.getName() + " DATA 파일 삭제 실패했습니다.");
        }

        // lay 파일 삭제
        try {
            FileSystemUtil.deleteFile(layFilePath);
        } catch (IOException e) {
            log.error(layer.getName() + " LAY 파일 삭제 실패했습니다.");
        }

        // INCLUDE lay 파일 내용 삭제
        try {
            MapServerUtil.removeLayerIncludeSyntaxInMapFiles(dataPath + Constants.MAP_FILE_PATH, layer.getName());
        } catch (IOException e) {
            log.error(layer.getName() + " INCLUDE LAY 파일 내용 삭제 실패했습니다.");
        }

        entityManager.remove(layer);
        return true;
    }

    // 사용 가능성이 미비하여 아래에 기재.
    private File findMapFile(File destDir) {
        File[] files = destDir.listFiles();
        for(File file : files){
            if(file.getName().lastIndexOf(".map") > -1){
                return file;
            }else if(file.isDirectory()){
                File f = findMapFile(file);
                if(f != null){
                    return f;
                }
            }
        }
        return null;
    }
}
