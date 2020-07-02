package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyGlobalDTO;
import com.jiin.admin.mapper.BaseMapper;

import java.util.List;

@BaseMapper
public interface ProxyGlobalMapper {
    long findNextSeqVal();
    List<ProxyGlobalDTO> findAll();
    int insert(ProxyGlobalDTO proxyGlobalDTO);
    void deleteAll();
}
