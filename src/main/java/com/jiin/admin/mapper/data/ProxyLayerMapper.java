package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxyLayerMapper {
    long findNextSeqVal();
    List<ProxyLayerDTO> findAll();
    List<ProxyLayerDTO> findBySelected(@Param("selected") boolean selected);
    int create(ProxyLayerDTO proxyLayerDTO);
    int update(ProxyLayerDTO proxyLayerDTO);
    int deleteById(@Param("id") long id);
}
