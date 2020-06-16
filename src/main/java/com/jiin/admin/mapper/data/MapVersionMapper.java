package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface MapVersionMapper {
    long findNextSeqVal();

    MapVersionDTO findByMapIdRecently(@Param("mapId") long mapId);
    MapVersionDTO findByMapIdAndLayerRecently(@Param("mapId") long mapId, @Param("layerId") long layerId);
    List<MapVersionDTO> findByMapId(@Param("mapId") long mapId);
    MapVersionDTO findByMapIdAndVersion(@Param("mapId") long mapId, @Param("version") double version);

    int insert(MapVersionDTO mapVersionDTO);
    int insertRelate(@Param("mapVersionId") long mapVersionId, @Param("mapLayerId") long layerId);

    int update(MapVersionDTO mapVersionDTO);

    int deleteByMapId(@Param("mapId") long mapId);
    int deleteRelateByVersionId(@Param("id") long id);
    int deleteRelateByLayerId(@Param("layerId") long layerId);
}
