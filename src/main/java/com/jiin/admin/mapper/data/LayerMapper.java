package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.LayerPageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface LayerMapper {
    List<LayerDTO> findAll();
    List<LayerDTO> findByPageModel(LayerPageModel layerPageModel);

    LayerDTO findById(@Param("id") long id);
    LayerDTO findByName(@Param("name") String name);
    List<LayerDTO> findByMapId(@Param("mapId") long mapId);
}
