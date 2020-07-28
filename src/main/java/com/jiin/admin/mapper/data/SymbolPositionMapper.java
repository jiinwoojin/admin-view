package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface SymbolPositionMapper {
    List<SymbolPositionDTO> findAll();
    int insert(SymbolPositionDTO symbolPositionDTO);
    int deleteById(@Param("id") long id);
    int deleteByImageId(@Param("imageId") long imageId);
}
