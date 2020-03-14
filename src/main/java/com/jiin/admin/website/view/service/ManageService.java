package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import com.jiin.admin.website.view.mapper.ManageMapper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.stream.Collectors.toList;

@Service
public class ManageService {

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ManageMapper mapper;

    @PersistenceContext
    EntityManager entityManager;

    public List<Map> getSourceList() {
        return mapper.getSourceList();
    }

    public List<Map> getLayerList() {
        List<Map> layers = mapper.getLayerList();
        for(Map layer : layers){
            List<Map> sources = mapper.getSourceListByLayerId((Long) layer.get("id"));
            List<Object> sourceIds = sources.stream().map(s -> s.get("id")).collect(toList());
            layer.put("source_ids", StringUtil.join(",",sourceIds));
        }
        return layers;
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
    public boolean delSource(Long sourceId) {
        long cnt = mapper.getLayerCountBySourceId(sourceId);
        if(cnt > 0){
            return false;
        }
        entityManager.remove(entityManager.find(MapSource.class,sourceId));
        return true;
    }

    @Transactional
    public boolean addLayer(String name, String title, Long[] sources, MultipartFile thumbnail) throws IOException {
        String thumbnailName = "empty";
        if(thumbnail != null && thumbnail.getSize() > 0){
            thumbnailName = UUID.randomUUID().toString();
            File thumbnailFile = new File(dataPath, "tmp/thumbnail/" + thumbnailName);
            thumbnail.transferTo(thumbnailFile);
        }
        //
        TypedQuery<MapSource> query = entityManager.createQuery(
                "SELECT T FROM " + MapSource.class.getAnnotation(Entity.class).name() + " T WHERE ID IN ("+StringUtil.join(sources, ",")+")"
                , MapSource.class);
        List<MapSource> sourceEntity = query.getResultList();
        //
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MapLayer entity = new MapLayer();
        entity.setDefault(false);
        entity.setName(name);
        entity.setTitle(title);
        entity.setSource(sourceEntity);
        entity.setThumbnail(thumbnailName);
        entity.setRegistorId(loginUser);
        entity.setRegistorName(loginUser);
        entity.setRegistTime(new Date());
        entityManager.persist(entity);
        return true;
    }

    @Transactional
    public boolean delLayer(Long layerId) {
        entityManager.remove(entityManager.find(MapLayer.class,layerId));
        return true;
    }
}
