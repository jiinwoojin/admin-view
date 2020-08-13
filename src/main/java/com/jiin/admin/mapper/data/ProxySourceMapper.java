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
    ProxySourceDTO findById(@Param("id") long id);
    ProxySourceDTO findByName(@Param("name") String name);
    List<ProxySourceDTO> findBySelected(@Param("selected") boolean selected);
    List<ProxySourceDTO> findByRequestMapEndsWith(@Param("requestMap") String requestMap);
    List<ProxySourceMapServerDTO> findBySelectedMapServer(@Param("selected") boolean selected);
    List<ProxySourceWMSDTO> findBySelectedWMS(@Param("selected") boolean selected);
    List<ProxySourceMapServerDTO> findByMapServerListRequestMapEndsWith(@Param("mapFilePath") String mapFilePath);
    List<ProxySourceWMSDTO> findByWMSListRequestMapEndsWith(@Param("mapFilePath") String mapFilePath);

    int insert(ProxySourceDTO proxySourceDTO);
    int insertMapServer(ProxySourceMapServerDTO proxySourceMapServerDTO);
    int insertWMS(ProxySourceWMSDTO proxySourceWMSDTO);

    int update(ProxySourceDTO proxySourceDTO);
    int updateByName(ProxySourceDTO proxySourceDTO);
    int updateMapServer(ProxySourceMapServerDTO proxySourceMapServerDTO);
    int updateWMS(ProxySourceWMSDTO proxySourceWMSDTO);
    int updateSelectedByNameIn(@Param("names") List<String> names, @Param("selected") boolean selected);
    int updateSelectedAllDisabled();

    int deleteById(@Param("id") long id);
    int deleteByName(@Param("name") String name);
    int deleteByIdMapServer(@Param("id") long id);
    int deleteByIdWMS(@Param("id") long id);
}
