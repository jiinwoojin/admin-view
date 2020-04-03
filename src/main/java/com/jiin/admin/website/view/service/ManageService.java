package com.jiin.admin.website.view.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.entity.MapLayerRelationEntity;
import com.jiin.admin.website.model.LayerSearchModel;
import com.jiin.admin.website.model.OptionModel;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<MapEntity> getSourceList() {
        return mapper.getSourceList();
    }

    public List<LayerEntity> getLayerList() {
        //List<Map<String, Object>> layers = mapper.getLayerList();
        /*for(Map<String, Object> layer : layers){
            List<Map<String, Object>> sources = mapper.getSourceListByLayerId((Long) layer.get("id"));
            List<Object> sourceIds = sources.stream().map(s -> s.get("id")).collect(toList());
            layer.put("source_ids", StringUtil.join(",",sourceIds));
        }*/
        return mapper.getLayerList();
    }

    // Get Layer List With Pagination Model
    public Map<String, Object> getLayerListByPaginationModel(LayerSearchModel paginationModel) throws ParseException {
        // Order By Field
        final Sort[] sorts = {
            Sort.by("id").descending(),
            Sort.by("id").ascending(),
            Sort.by("name").ascending(),
            Sort.by("regist_time").descending(),
        };

        Pageable pageable = PageRequest.of(paginationModel.getPg() - 1, paginationModel.getSz(), sorts[paginationModel.getOb()]);

        // Search By Keyword
        List<LayerEntity> sbRes = new ArrayList<>();
        switch(paginationModel.getSb()){
            // find By Default
            case 0 :
                sbRes = mapper.getLayerList();
                break;

            // find By Name
            case 1 :
                sbRes = mapper.getLayerList()
                            .stream()
                            .filter(layer -> layer.getName().contains(paginationModel.getSt()))
                            .collect(Collectors.toList());
                break;

            // find By Registor
            case 2 :
                sbRes = mapper.getLayerList()
                        .stream()
                        .filter(layer -> layer.getRegistorId().contains(paginationModel.getSt()))
                        .collect(Collectors.toList());
                break;

            // find By Projection (EPSG)
            case 3 :
                sbRes = mapper.getLayerList()
                        .stream()
                        .filter(layer -> layer.getProjection().contains(paginationModel.getSt()))
                        .collect(Collectors.toList());
                break;
        }

        // Date Separator
        String sDate = paginationModel.getSDate();
        String eDate = paginationModel.getEDate();
        if(sDate != null && eDate != null){
            if(sDate.compareTo(eDate) < 0) {
                Date start = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
                Date end = new SimpleDateFormat("yyyy-MM-dd").parse(eDate);
                sbRes = sbRes.stream()
                        .filter(layer -> layer.getRegistTime().after(start) && layer.getRegistTime().before(end))
                        .collect(Collectors.toList());
            }
        }

        // Type Separator
        switch(paginationModel.getLType()){
            case "RASTER" :
                sbRes = sbRes.stream()
                        .filter(layer -> layer.getType().equalsIgnoreCase("RASTER"))
                        .collect(Collectors.toList());
                break;
            case "VECTOR" :
                sbRes = sbRes.stream()
                        .filter(layer -> layer.getType().equalsIgnoreCase("VECTOR"))
                        .collect(Collectors.toList());
                break;

            /*
             * 나머지는 RASTER, VECTOR 모든 타입이 나오는 걸로 작동.
             */
        }

        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();
        long pageEnd = (pageOffset + pageSize) > sbRes.size() ? sbRes.size() : pageOffset + pageSize;

        Page<LayerEntity> page = new PageImpl<>(sbRes.subList((int) pageOffset, (int) pageEnd), pageable, sbRes.size());

        Map<String, Object> map = new HashMap<>();
        map.put("count", page.getTotalElements());
        map.put("data", page.getContent());

        return map;
    }

    public List<OptionModel> searchByOptions(){
        return Arrays.asList(
            new OptionModel("-- 검색 키워드 선택 --", 0),
            new OptionModel("레이어 이름", 1),
            new OptionModel("레이어 등록자", 2),
            new OptionModel("레이어 좌표 체계", 3)
        );
    }

    public List<OptionModel> orderByOptions(){
        return Arrays.asList(
            new OptionModel("-- 정렬 방식 선택 --", 0),
            new OptionModel("ID 순서 정렬", 1),
            new OptionModel("이름 순서 정렬", 2),
            new OptionModel("등록 기간", 3)
        );
    }

    @Transactional
    public boolean addMap(MapEntity map, String layerList) throws IOException {
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.setDefault(false);
        map.setRegistorId(loginUser);
        map.setRegistorName(loginUser);
        map.setRegistTime(new Date());

        List<java.util.Map<String, Object>> orderLayerList = new ObjectMapper().readValue(layerList, new TypeReference<LinkedList<java.util.Map<String, Object>>>(){});

        StringBuilder layerBuilder = new StringBuilder();

        for (java.util.Map<String, Object> orderLayer : orderLayerList) {
            LayerEntity layer = entityManager.find(LayerEntity.class, Long.parseLong(String.valueOf(orderLayer.get("layerId"))));
            MapLayerRelationEntity mapLayerRelation = new MapLayerRelationEntity();
            mapLayerRelation.setMap(map);
            mapLayerRelation.setLayer(layer);
            mapLayerRelation.setLayerOrder(Integer.parseInt(String.valueOf(orderLayer.get("order"))));

            map.getMapLayerRelations().add(mapLayerRelation);

            layerBuilder.append("  INCLUDE \"./layer/").append(layer.getName()).append(Constants.LAY_SUFFIX).append("\"");
            layerBuilder.append(System.lineSeparator());
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

        if (FileUtils.getFile(mapFilePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(mapFilePath));
        }

        File mapFile = new File(mapFilePath);

        FileUtils.write(mapFile, stringBuilder.toString(), "utf-8");

        setPermission(mapFile.toPath());

        map.setMapFilePath(Objects.requireNonNull(mapFilePath).replaceAll(dataPath, ""));

        entityManager.persist(map);
        return true;
    }

    /**
     * 파일 권한 설정
     * @param file Path
     * @throws IOException Exception
     */
    private void setPermission(Path file) throws IOException{
        Set<PosixFilePermission> permissionSet = Files.readAttributes(file, PosixFileAttributes.class).permissions();

        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        permissionSet.add(PosixFilePermission.GROUP_WRITE);
        permissionSet.add(PosixFilePermission.GROUP_READ);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        permissionSet.add(PosixFilePermission.OTHERS_WRITE);
        permissionSet.add(PosixFilePermission.OTHERS_READ);
        permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions(file, permissionSet);
    }

    /*@Transactional
    public boolean addSource(String name, String type, String desc, MultipartFile file) {
        Path destDir;
        try{
            destDir = Files.createTempDirectory("temp");
            System.out.println(">> unzip > " + destDir);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(file.getInputStream());
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File destFile = new File(destDir.toFile(), zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    destFile.mkdirs();
                    continue;
                }
                FileOutputStream fos = new FileOutputStream(destFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.closeEntry();
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //
        File mapFile = findMapFile(destDir.toFile());
        if(mapFile == null){
            return false;
        }
        String layer = null;
        try {
            Scanner reader = new Scanner(mapFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if(line.trim().equals("LAYER")){
                    String layerGroup = reader.nextLine().split("\"")[1];
                    String layerName = reader.nextLine().split("\"")[1];
                    layer = layerGroup + ":" + layerName;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if(layer == null){
            return false;
        }
        String sourceDir = UUID.randomUUID().toString();
        File dest = new File(dataPath + "/mapserver/" + sourceDir);
        try {
            FileUtils.copyDirectory(mapFile.getParentFile(), dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MapSource entity = new MapSource();
        entity.setDefault(false);
        entity.setName(name);
        entity.setType(type);
        entity.setDescription(desc);
        entity.setMapPath(dataPath + "/mapserver/"+ sourceDir + "/" + mapFile.getName());
        entity.setLayers(layer); // TODO : 추출작업 필요
        entity.setUseCache(true);
        entity.setCacheName(name + "_CACHE");
        entity.setRegistorId(loginUser);
        entity.setRegistorName(loginUser);
        entity.setRegistTime(new Date());
        entityManager.persist(entity);
        return true;
    }*/

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

    @Transactional
    public boolean delMap(Long mapId) {
        /*long cnt = mapper.getLayerCountBySourceId(sourceId);
        if(cnt > 0){
            return false;
        }*/
        MapEntity map = entityManager.find(MapEntity.class, mapId);

        String mapFilePath = dataPath + map.getMapFilePath();

        // lay 파일 삭제
        try {
            if (FileUtils.getFile(mapFilePath).isFile()) {
                FileUtils.forceDelete(FileUtils.getFile(mapFilePath));
            }
        } catch (IOException e) {
            log.error(map.getName() + " LAY 파일 삭제 실패했습니다.");
        }

        entityManager.remove(map);
        return true;
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
     * @throws IOException
     */
    @Transactional
    public boolean addLayer(String name, String description, String projection, String middle_folder, String type, MultipartFile data_file) throws IOException {
        log.info("Folder : " + middle_folder);
        log.info("Data Folder : " + dataPath + Constants.DATA_PATH + "/" + middle_folder);

        String filePath = null;
        File dataFile;
        if(data_file != null && data_file.getSize() > 0){
            String dirPath = dataPath + Constants.DATA_PATH + "/" + middle_folder;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            filePath = dirPath + "/" + data_file.getOriginalFilename();
            dataFile = new File(filePath);
            data_file.transferTo(dataFile);

            setPermission(dataFile.toPath());
        }

        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LayerEntity layer = new LayerEntity();
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
        String layFilePath = dataPath + Constants.LAY_FILE_PATH + "/" + name + Constants.LAY_SUFFIX;

        if (FileUtils.getFile(layFilePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(layFilePath));
        }

        File layFile = new File(layFilePath);

        FileUtils.write(layFile, stringBuilder.toString(), "utf-8");

        setPermission(layFile.toPath());

        layer.setLayerFilePath(Objects.requireNonNull(layFilePath).replaceAll(dataPath, ""));

        entityManager.persist(layer);

        return true;
    }

    @Transactional
    public boolean delLayer(Long layerId) {
        LayerEntity layer = entityManager.find(LayerEntity.class, layerId);

        // DATA 파일 삭제
        String dataFilePath = dataPath + layer.getDataFilePath();
        String layFilePath = dataPath + layer.getLayerFilePath();
        try {
            if (FileUtils.getFile(dataFilePath).isFile()) {
                FileUtils.forceDelete(FileUtils.getFile(dataFilePath));
            }
        } catch (IOException e) {
            log.error(layer.getName() + " DATA 파일 삭제 실패했습니다.");
        }

        // lay 파일 삭제
        try {
            if (FileUtils.getFile(layFilePath).isFile()) {
                FileUtils.forceDelete(FileUtils.getFile(layFilePath));
            }
        } catch (IOException e) {
            log.error(layer.getName() + " LAY 파일 삭제 실패했습니다.");
        }

        entityManager.remove(layer);
        return true;
    }
}
