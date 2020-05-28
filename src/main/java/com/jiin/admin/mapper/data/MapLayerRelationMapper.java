package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.MapLayerRelationDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

@BaseMapper
public interface MapLayerRelationMapper {
    int insert(MapLayerRelationDTO mapLayerRelationDTO);
    int deleteByMapId(@Param("mapId") long mapId);
    int deleteByLayerId(@Param("layerId") long layerId);
}
