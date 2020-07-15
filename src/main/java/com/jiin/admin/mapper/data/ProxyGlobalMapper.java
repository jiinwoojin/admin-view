package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyGlobalDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxyGlobalMapper {
    long findNextSeqVal();
    List<ProxyGlobalDTO> findAll();
    ProxyGlobalDTO findByKey(@Param("key") String key);
    int insert(ProxyGlobalDTO proxyGlobalDTO);
    void deleteAll();
}
