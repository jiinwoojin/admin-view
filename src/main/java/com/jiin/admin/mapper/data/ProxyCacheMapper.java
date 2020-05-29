package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxyCacheMapper {
    long findNextSeqVal();
    List<ProxyCacheDTO> findAll();
    List<ProxyCacheDTO> findBySelected(@Param("selected") boolean selected);
    int create(ProxyCacheDTO proxyCacheDTO);
    int update(ProxyCacheDTO proxyCacheDTO);
    int deleteById(@Param("id") long id);
}
