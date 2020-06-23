package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.dto.ProxySourceMapServerDTO;
import com.jiin.admin.dto.ProxySourceWMSDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxySourceMapper {
    long findNextSeqVal();

    List<ProxySourceDTO> findAll();
    List<ProxySourceMapServerDTO> findAllMapServer();
    List<ProxySourceWMSDTO> findAllWMS();
    ProxySourceDTO findByName(@Param("name") String name);
    List<ProxySourceDTO> findBySelected(@Param("selected") boolean selected);

    int insert(ProxySourceDTO proxySourceDTO);
    int insertMapServer(ProxySourceMapServerDTO proxySourceMapServerDTO);
    int insertWMS(ProxySourceWMSDTO proxySourceWMSDTO);

    int update(ProxySourceDTO proxySourceDTO);
    int updateMapServer(ProxySourceMapServerDTO proxySourceMapServerDTO);
    int updateWMS(ProxySourceWMSDTO proxySourceWMSDTO);
    int updateSelectedByNameIn(@Param("names") List<String> names, @Param("selected") boolean selected);
    int updateSelectedAllDisabled();

    int deleteById(@Param("id") long id);
    int deleteByIdMapServer(@Param("id") long id);
    int deleteByIdWMS(@Param("id") long id);
}
