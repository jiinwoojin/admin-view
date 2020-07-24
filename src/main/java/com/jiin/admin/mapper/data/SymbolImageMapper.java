package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.mapper.BaseMapper;

import java.util.List;

@BaseMapper
public interface SymbolImageMapper {
    List<SymbolImageDTO> findAll();
}
