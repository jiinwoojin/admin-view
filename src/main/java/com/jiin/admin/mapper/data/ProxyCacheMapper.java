package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ProxyCacheMapper {
    long findNextSeqVal();
    List<ProxyCacheDTO> findAll();
    ProxyCacheDTO findByName(@Param("name") String name);
    List<ProxyCacheDTO> findBySelected(@Param("selected") boolean selected);
    int insert(ProxyCacheDTO proxyCacheDTO);
    int update(ProxyCacheDTO proxyCacheDTO);
    int updateSelectedByNameIn(@Param("names") List<String> names, @Param("selected") boolean selected);
    int updateSelectedAllDisabled();
    int deleteById(@Param("id") long id);
}
