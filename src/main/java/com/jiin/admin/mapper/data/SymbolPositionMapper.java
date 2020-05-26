package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.BaseMapper;

import java.util.List;

@BaseMapper
public interface SymbolPositionMapper {
    List<SymbolPositionDTO> findAll();
}
