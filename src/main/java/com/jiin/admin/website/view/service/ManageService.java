package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import com.jiin.admin.website.view.mapper.ManageMapper;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

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
        return mapper.getLayerList();
    }

    @Transactional
    public boolean addSource(String name, String type, String desc, MultipartFile file) {
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MapSource entity = new MapSource();
        entity.setDefault(false);
        entity.setName(name);
        entity.setType(type);
        entity.setDescription(desc);
        entity.setMapPath(dataPath + "/mapserver/world_k2/world_k2.map"); // TODO : 추출작업 필요
        entity.setLayers("world"); // TODO : 추출작업 필요
        entity.setUseCache(true);
        entity.setCacheName(name + "_CACHE");
        entity.setRegistorId(loginUser);
        entity.setRegistorName(loginUser);
        entity.setRegistTime(new Date());
        entityManager.persist(entity);
        return true;
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
