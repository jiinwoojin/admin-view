package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyGlobalDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@BaseMapper
public interface ProxyGlobalMapper {
    long findNextSeqVal();
    List<ProxyGlobalDTO> findAll();
    int insert(ProxyGlobalDTO proxyGlobalDTO);
    int update(ProxyGlobalDTO proxyGlobalDTO);
    int deleteById(@Param("id") long id);
    int deleteByIdListNotIn(@Param("ids") Set<Long> ids);
}
