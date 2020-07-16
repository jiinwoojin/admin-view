package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface ProxyLayerMapper {
    long findNextSeqVal();
    List<ProxyLayerDTO> findAll();
    ProxyLayerDTO findById(@Param("id") long id);
    ProxyLayerDTO findByName(@Param("name") String name);
    List<ProxyLayerDTO> findBySelected(@Param("selected") boolean selected);
    List<Map<String, Object>> findLayerSourcesCountAndSourceContains(@Param("sourceId") long sourceId);
    List<Map<String, Object>> findLayerCachesCountAndCacheContains(@Param("cacheId") long cacheId);
    int insert(ProxyLayerDTO proxyLayerDTO);
    int update(ProxyLayerDTO proxyLayerDTO);
    int updateByName(ProxyLayerDTO proxyLayerDTO);
    int updateSelectedByNameIn(@Param("names") List<String> names, @Param("selected") boolean selected);
    int updateSelectedAllDisabled();
    int deleteById(@Param("id") long id);
    int deleteByName(@Param("name") String name);
}
