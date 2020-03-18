package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.Layer;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface ManageMapper {

    @Select("SELECT * FROM _LAYER")
    List<Layer> getLayerList();

    @Select("SELECT * FROM _MAP")
    List<com.jiin.admin.entity.Map> getSourceList();

    @Select("SELECT S.* FROM _MAP_SOURCE S INNER JOIN _MAP_LAYER_SOURCE LS ON S.ID = LS.SOURCE_ID WHERE LS.LAYER_ID = #{layerId}")
    List<Map<String, Object>> getSourceListByLayerId(@Param("layerId") Long layerId);

    @Select("SELECT COUNT(1) FROM _MAP_LAYER L INNER JOIN _MAP_LAYER_SOURCE LS ON L.ID = LS.LAYER_ID WHERE LS.SOURCE_ID = #{sourceId}")
    long getLayerCountBySourceId(@Param("sourceId") Long sourceId);
}
