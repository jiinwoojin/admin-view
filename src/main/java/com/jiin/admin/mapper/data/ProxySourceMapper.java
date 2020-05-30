package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxySourceMapper {
    List<ProxySourceDTO> findAll();
    List<ProxySourceDTO> findBySelected(@Param("selected") boolean selected);
    int create(ProxySourceDTO proxySourceDTO);
    int update(ProxySourceDTO proxySourceDTO);
    int deleteById(@Param("id") long id);
}
