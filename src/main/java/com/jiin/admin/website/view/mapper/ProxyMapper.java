package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.ProxyCacheEntity;
import com.jiin.admin.entity.ProxyLayerEntity;
import com.jiin.admin.entity.ProxySourceEntity;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySourceModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface ProxyMapper {
    @Select("SELECT * FROM _PROXY_CACHE")
    List<ProxyCacheEntity> findAllProxyCacheEntities();

    @Select("SELECT * FROM _PROXY_LAYER")
    List<ProxyLayerEntity> findAllProxyLayerEntities();

    @Select("SELECT * FROM _PROXY_SOURCE")
    List<ProxySourceEntity> findAllProxySourceEntities();

    @Select("SELECT * FROM _PROXY_LAYER WHERE SELECTED = true")
    List<ProxyLayerEntity> findProxyLayerSelectedEntities();

    @Select("SELECT * FROM _PROXY_SOURCE WHERE SELECTED = true")
    List<ProxySourceEntity> findProxySourceSelectedEntities();

    @Select("SELECT * FROM _PROXY_CACHE WHERE SELECTED = true")
    List<ProxyCacheEntity> findProxyCacheSelectedEntities();

    @Select("SELECT * FROM _PROXY_LAYER ORDER BY ID DESC LIMIT 1")
    ProxyLayerEntity findRecentlyInsertingProxyLayerEntity();

    @Select("SELECT * FROM _PROXY_CACHE ORDER BY ID DESC LIMIT 1")
    ProxyCacheEntity findRecentlyInsertingProxyCacheEntity();

    @Select("SELECT * FROM _PROXY_SOURCE WHERE NAME = #{name} ORDER BY ID DESC LIMIT 1")
    ProxySourceEntity findProxySourceEntityWithName(Map<String, String> map);

    @Insert("INSERT INTO _PROXY_LAYER(ID, NAME, TITLE, SELECTED) VALUES(#{id}, #{proxyLayerName}, #{proxyLayerTitle}, false)")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_LAYER_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyLayerWithModel(ProxyLayerModel proxyLayerModel);

    @Insert("INSERT INTO _PROXY_SOURCE(ID, NAME, TYPE, REQUEST_MAP, REQUEST_LAYERS, MAP_SERVER_BINARY, MAP_SERVER_WORK_DIR, SELECTED) VALUES(#{id}, #{proxySourceName}, #{proxySourceType}, #{requestMap}, #{requestLayers}, #{mapServerBinary}, #{mapServerWorkDir}, false)")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxySourceWithModel(ProxySourceModel proxySourceModel);

    @Insert("INSERT INTO _PROXY_CACHE(ID, NAME, CACHE_TYPE, CACHE_DIRECTORY, META_SIZE_X, META_SIZE_Y, META_BUFFER, SELECTED) VALUES(#{id}, #{proxyCacheName}, #{proxyCacheType}, #{proxyCacheDirectory}, #{metaSizeX}, #{metaSizeY}, #{metaBuffer}, false)")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_CACHE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyCacheWithModel(ProxyCacheModel proxyCacheModel);

    @Insert("INSERT INTO _PROXY_LAYER_SOURCE_RELATION(ID, LAYER_ID, SOURCE_ID) VALUES(#{id}, #{layerId}, #{sourceId})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_LAYER_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyLayerSourceRelationWithMap(Map<String, Long> map);

    @Insert("INSERT INTO _PROXY_CACHE_SOURCE_RELATION(ID, CACHE_ID, SOURCE_ID) VALUES(#{id}, #{cacheId}, #{sourceId})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_CACHE_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyCacheSourceRelationWithMap(Map<String, Long> map);
}
