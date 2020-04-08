package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.ProxyCacheEntity;
import com.jiin.admin.entity.ProxyLayerEntity;
import com.jiin.admin.entity.ProxySourceEntity;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@BaseMapper
public interface ProxyMapper {
    @Select("SELECT * FROM _PROXY_CACHE")
    List<ProxyCacheEntity> findAllProxyCacheEntities();

    @Select("SELECT * FROM _PROXY_LAYER")
    List<ProxyLayerEntity> findAllProxyLayerEntities();

    @Select("SELECT * FROM _PROXY_SOURCE")
    List<ProxySourceEntity> findAllProxySourceEntities();

    @Select("SELECT * FROM _PROXY_CACHE WHERE SELECTED = true")
    List<ProxyCacheEntity> findProxyCacheSelectedEntities();

    @Select("SELECT * FROM _PROXY_LAYER WHERE SELECTED = true")
    List<ProxyLayerEntity> findProxyLayerSelectedEntities();

    @Select("SELECT * FROM _PROXY_SOURCE WHERE SELECTED = true")
    List<ProxySourceEntity> findProxySourceSelectedEntities();
}
