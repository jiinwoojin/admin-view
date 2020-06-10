package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxyLayerMapper {
    long findNextSeqVal();
    List<ProxyLayerDTO> findAll();
    ProxyLayerDTO findByName(@Param("name") String name);
    List<ProxyLayerDTO> findBySelected(@Param("selected") boolean selected);
    int insert(ProxyLayerDTO proxyLayerDTO);
    int update(ProxyLayerDTO proxyLayerDTO);
    int updateSelectedByNameIn(@Param("names") List<String> names, @Param("selected") boolean selected);
    int updateSelectedAllDisabled();
    int deleteById(@Param("id") long id);
}
