package com.jiin.admin.website.view.mapper;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.entity.*;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySourceModel;
import org.apache.ibatis.annotations.*;

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

//    /*
//     * TODO : MyBatis 로 Proxy Mapper 관계 ( 1:1 및 1:N ) 구현 방안 재구상. 지금은 JPA 를 사용해서 구현할 계획.
//     */
//    @Select({
//        "SELECT c.id cacheId, c.name cacheName, c.meta_size_x metaSizeX, c.meta_size_y metaSizeY, c.meta_buffer metaBuffer, c.cache_type cacheType, c.cache_directory cacheDirectory, c.selected cacheSelected, s.*, cs.source_id sourceId",
//        "FROM _PROXY_CACHE c LEFT JOIN _PROXY_CACHE_SOURCE_RELATION cs ON cs.cache_id = c.id LEFT JOIN _PROXY_SOURCE s ON cs.source_id = s.id"
//    })
//    @Results(id="ProxyCacheDTO", value = {
//        @Result(property = "id", column = "cacheId"),
//        @Result(property = "name", column = "cacheName"),
//        @Result(property = "metaSizeX", column = "metaSizeX"),
//        @Result(property = "metaSizeY", column = "metaSizeY"),
//        @Result(property = "metaBuffer", column = "metaBuffer"),
//        @Result(property = "cacheType", column = "cacheType"),
//        @Result(property = "cacheDirectory", column = "cacheDirectory"),
//        @Result(property = "selected", column = "cacheSelected"),
//        @Result(property = "sources", javaType = List.class, column = "sourceId", many = @Many(select = "findProxySourceDTOsById"))
//    })
//    List<ProxyCacheDTO> findAllProxyCacheDTOs();
//
//    @Select({
//            "SELECT l.id layerId, l.name layerName, l.title layerTitle, l.selected layerSelected, s.*, ls.source_id sourceId",
//            "FROM _PROXY_LAYER l LEFT JOIN _PROXY_LAYER_SOURCE_RELATION ls ON ls.layer_id = l.id LEFT JOIN _PROXY_SOURCE s ON ls.source_id = s.id"
//    })
//    @Results(id="ProxyLayerDTO", value = {
//            @Result(property = "id", column = "layerId"),
//            @Result(property = "name", column = "layerName"),
//            @Result(property = "title", column = "layerTitle"),
//            @Result(property = "selected", column = "layerSelected"),
//            @Result(property = "sources", javaType = List.class, column = "sourceId", many = @Many(select = "findProxySourceDTOsById")),
//            @Result(property = "caches", javaType = List.class, column = "cacheId", many = @Many(select = "findProxySourceDTOsById"))
//    })
//    List<ProxyLayerDTO> findAllProxyLayerDTOs();
//
//    @Select("SELECT * FROM _PROXY_SOURCE WHERE id = #{id}")
//    ProxySourceEntity findProxySourceDTOsById(@Param("id") long id);
//
//    @Select("SELECT * FROM _PROXY_CACHE WHERE id = #{id}")
//    ProxyCacheEntity findProxyCacheDTOsById(@Param("id") long id);

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

    @Select("SELECT * FROM _PROXY_CACHE WHERE NAME = #{name} ORDER BY ID DESC LIMIT 1")
    ProxyCacheEntity findProxyCacheEntityWithName(Map<String, String> map);

    @Insert("INSERT INTO _PROXY_LAYER(ID, NAME, TITLE, SELECTED, IS_DEFAULT) VALUES(#{id}, #{proxyLayerName}, #{proxyLayerTitle}, false, #{isDefault})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_LAYER_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyLayerWithModel(ProxyLayerModel proxyLayerModel);

    @Insert("INSERT INTO _PROXY_SOURCE(ID, NAME, TYPE, REQUEST_MAP, REQUEST_LAYERS, MAP_SERVER_BINARY, MAP_SERVER_WORK_DIR, SELECTED, IS_DEFAULT) VALUES(#{id}, #{proxySourceName}, #{proxySourceType}, #{requestMap}, #{requestLayers}, #{mapServerBinary}, #{mapServerWorkDir}, false, #{isDefault})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxySourceWithModel(ProxySourceModel proxySourceModel);

    @Insert("INSERT INTO _PROXY_CACHE(ID, NAME, CACHE_TYPE, CACHE_DIRECTORY, META_SIZE_X, META_SIZE_Y, META_BUFFER, SELECTED, IS_DEFAULT) VALUES(#{id}, #{proxyCacheName}, #{proxyCacheType}, #{proxyCacheDirectory}, #{metaSizeX}, #{metaSizeY}, #{metaBuffer}, false, #{isDefault})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_CACHE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyCacheWithModel(ProxyCacheModel proxyCacheModel);

    @Insert("INSERT INTO _PROXY_LAYER_SOURCE_RELATION(ID, LAYER_ID, SOURCE_ID) VALUES(#{id}, #{layerId}, #{sourceId})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_LAYER_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyLayerSourceRelationWithMap(Map<String, Long> map);

    @Insert("INSERT INTO _PROXY_LAYER_CACHE_RELATION(ID, LAYER_ID, CACHE_ID) VALUES(#{id}, #{layerId}, #{cacheId})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_LAYER_CACHE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyLayerCacheRelationWithMap(Map<String, Long> map);

    @Insert("INSERT INTO _PROXY_CACHE_SOURCE_RELATION(ID, CACHE_ID, SOURCE_ID) VALUES(#{id}, #{cacheId}, #{sourceId})")
    @SelectKey(statement="SELECT NEXTVAL('PROXY_CACHE_SOURCE_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertProxyCacheSourceRelationWithMap(Map<String, Long> map);

    @Update({
        "UPDATE _PROXY_LAYER",
        "SET TITLE = #{proxyLayerTitle}",
        "WHERE id = #{id}"
    })
    void updateProxyLayerWithModel(ProxyLayerModel proxyLayerModel);

    @Update({
        "UPDATE _PROXY_SOURCE",
        "SET TYPE = #{proxySourceType}, REQUEST_MAP = #{requestMap}, REQUEST_LAYERS = #{requestLayers}, MAP_SERVER_BINARY = #{mapServerBinary}, MAP_SERVER_WORK_DIR = #{mapServerWorkDir}",
        "WHERE id = #{id}"
    })
    void updateProxySourceWithModel(ProxySourceModel proxySourceModel);

    @Update({
        "UPDATE _PROXY_CACHE",
        "SET META_SIZE_X = #{metaSizeX}, META_SIZE_Y = #{metaSizeY}, META_BUFFER = #{metaBuffer}, CACHE_TYPE = #{proxyCacheType}, CACHE_DIRECTORY = #{proxyCacheDirectory}",
        "WHERE id = #{id}"
    })
    void updateProxyCacheWithModel(ProxyCacheModel proxyCacheModel);

    @Update({
        "UPDATE ${tableName}",
        "SET SELECTED = #{selected}",
        "WHERE NAME = #{name}"
    })
    void updateProxyEntitySelectedByName(@Param("tableName") String tableName, @Param("selected") Boolean selected, @Param("name") String name);

    @Update({
        "UPDATE ${tableName}",
        "SET SELECTED = #{selected}"
    })
    void updateProxyEntitySelected(@Param("tableName") String tableName, @Param("selected") Boolean selected);

    @Delete("DELETE FROM _PROXY_LAYER WHERE ID = #{id}")
    void deleteProxyLayerById(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_SOURCE WHERE ID = #{id}")
    void deleteProxySourceById(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_CACHE WHERE ID = #{id}")
    void deleteProxyCacheById(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_LAYER_SOURCE_RELATION WHERE LAYER_ID = #{id}")
    void deleteProxyLayerSourceRelationByLayerId(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_LAYER_SOURCE_RELATION WHERE SOURCE_ID = #{id}")
    void deleteProxyLayerSourceRelationBySourceId(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_LAYER_CACHE_RELATION WHERE LAYER_ID = #{id}")
    void deleteProxyLayerCacheRelationByLayerId(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_LAYER_CACHE_RELATION WHERE CACHE_ID = #{id}")
    void deleteProxyLayerCacheRelationByCacheId(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_CACHE_SOURCE_RELATION WHERE CACHE_ID = #{id}")
    void deleteProxyCacheSourceRelationByCacheId(Map<String, Long> map);

    @Delete("DELETE FROM _PROXY_CACHE_SOURCE_RELATION WHERE SOURCE_ID = #{id}")
    void deleteProxyCacheSourceRelationBySourceId(Map<String, Long> map);
}
