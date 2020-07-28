package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.SymbolPageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface SymbolImageMapper {
    long findNextSeqVal();
    long countByPageModel(SymbolPageModel symbolPageModel);
    List<SymbolImageDTO> findAll();
    List<SymbolImageDTO> findByPageModel(SymbolPageModel symbolPageModel);
    SymbolImageDTO findById(@Param("id") long id);
    SymbolImageDTO findByName(@Param("name") String name);
    int insert(SymbolImageDTO symbolImageDTO);
}
