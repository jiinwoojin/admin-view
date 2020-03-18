package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.entity.Layer;
import com.jiin.admin.entity.Map;
import com.jiin.admin.entity.MapLayerRelation;
import com.jiin.admin.entity.MapSource;
import com.jiin.admin.website.view.mapper.ManageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public List<Map> getSourceList() {
        return mapper.getSourceList();
    }

    public List<Layer> getLayerList() {
        //List<Map<String, Object>> layers = mapper.getLayerList();
        /*for(Map<String, Object> layer : layers){
            List<Map<String, Object>> sources = mapper.getSourceListByLayerId((Long) layer.get("id"));
            List<Object> sourceIds = sources.stream().map(s -> s.get("id")).collect(toList());
            layer.put("source_ids", StringUtil.join(",",sourceIds));
        }*/
        return mapper.getLayerList();
    }

    @Transactional
    public boolean addMap(com.jiin.admin.entity.Map map) throws IOException {
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.setDefault(false);
        map.setRegistorId(loginUser);
        map.setRegistorName(loginUser);
        map.setRegistTime(new Date());

        List<Layer> layers = mapper.getLayerList();

        int layerCount = 0;

        for (Layer layer : layers) {
            MapLayerRelation mapLayerRelation = new MapLayerRelation();
            mapLayerRelation.setMap(map);
            mapLayerRelation.setLayer(layer);
            mapLayerRelation.setLayerOrder(++layerCount);

            map.getMapLayerRelations().add(mapLayerRelation);
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
                    for (Layer layer : layers) {
                        stringBuilder.append("  INCLUDE ./").append(layer.getName()).append(".").append(Constants.LAY_SUFFIX);
                        stringBuilder.append(System.lineSeparator());
                    }
                }

                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // lay 파일 생성
        String mapFilePath = dataPath + Constants.MAP_FILE_PATH + "/" + map.getName() + Constants.MAP_SUFFIX;

        if (FileUtils.getFile(mapFilePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(mapFilePath));
        }

        FileUtils.write(new File(mapFilePath), stringBuilder.toString(), "utf-8");

        map.setMapFilePath(Objects.requireNonNull(mapFilePath).replaceAll(dataPath, ""));

        entityManager.persist(map);
        return true;
    }

    @Transactional
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
    }

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
        Map map = entityManager.find(Map.class, mapId);

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
        if(data_file != null && data_file.getSize() > 0){
            String dirPath = dataPath + Constants.DATA_PATH + "/" + middle_folder;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            filePath = dirPath + "/" + data_file.getOriginalFilename();
            File thumbnailFile = new File(filePath);
            data_file.transferTo(thumbnailFile);
        }

        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Layer layer = new Layer();
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
                    line = line.replaceAll("DATA_VALUE", "../.." + layer.getDataFilePath());
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

        FileUtils.write(new File(layFilePath), stringBuilder.toString(), "utf-8");

        layer.setLayerFilePath(Objects.requireNonNull(layFilePath).replaceAll(dataPath, ""));

        entityManager.persist(layer);

        return true;
    }

    @Transactional
    public boolean delLayer(Long layerId) {
        Layer layer = entityManager.find(Layer.class, layerId);

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
