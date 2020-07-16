package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.MapPageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface MapMapper {
    long findNextSeqVal();
    long countByPageModel(MapPageModel mapPageModel);

    List<MapDTO> findAll();
    List<MapDTO> findByPageModel(MapPageModel mapPageModel);

    MapDTO findById(@Param("id") long id);
    MapDTO findByName(@Param("name") String name);
    List<Map<String, Object>> findMapLayersCountAndLayerContains(@Param("layerId") long layerId);

    int insert(MapDTO mapDTO);
    int update(MapDTO mapDTO);

    int deleteById(long id);
}
