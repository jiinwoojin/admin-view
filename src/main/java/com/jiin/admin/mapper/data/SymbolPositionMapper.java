package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.SymbolPositionModel;
import com.jiin.admin.website.model.SymbolPositionPageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface SymbolPositionMapper {
    long countByImageId(@Param("imageId") long imageId);
    long countByPageModel(SymbolPositionPageModel symbolPositionPageModel);
    List<SymbolPositionDTO> findAll();
    SymbolPositionDTO findById(@Param("id") long id);
    List<SymbolPositionDTO> findByPageModel(SymbolPositionPageModel symbolPositionPageModel);
    SymbolPositionDTO findByNameAndImageId(@Param("name") String name, @Param("imageId") long imageId);
    int insert(SymbolPositionDTO symbolPositionDTO);
    int updateByModelAndImageId(SymbolPositionModel symbolPositionModel);
    int deleteById(@Param("id") long id);
    int deleteByImageId(@Param("imageId") long imageId);
    int deleteByIdIn(@Param("ids") List<Long> ids);
}
