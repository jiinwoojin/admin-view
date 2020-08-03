package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.SymbolImagePageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface SymbolImageMapper {
    long findNextSeqVal();
    long countByPageModel(SymbolImagePageModel symbolImagePageModel);
    List<SymbolImageDTO> findAll();
    List<SymbolImageDTO> findByPageModel(SymbolImagePageModel symbolImagePageModel);
    SymbolImageDTO findById(@Param("id") long id);
    SymbolImageDTO findByName(@Param("name") String name);
    int insert(SymbolImageDTO symbolImageDTO);
    int update(SymbolImageDTO symbolImageDTO);
    int deleteById(@Param("id") long id);
}
