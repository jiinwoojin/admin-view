package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.LayerSearchModel;
import com.jiin.admin.website.model.MapSearchModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface ManageMapper {

    @Select("SELECT * FROM _LAYER")
    List<LayerEntity> getLayerList();

    @Select("SELECT * FROM _MAP")
    List<MapEntity> getSourceList();

    @Select("SELECT M.* FROM _MAP M WHERE M.NAME = #{name}")
    MapEntity findMapEntityByName(@Param("name") String name);

    @Select({
        "<script>",
            "SELECT * FROM _LAYER L ",
            "<where>",
                "<choose>",
                    "<when test='sb == 1'> L.NAME LIKE CONCAT('%', #{st}, '%') </when>",
                    "<when test='sb == 2'> L.REGISTOR_ID LIKE CONCAT('%', #{st}, '%') </when>",
                    "<when test='sb == 3'> LOWER(L.PROJECTION) LIKE CONCAT('%', #{st}, '%') </when>",
                    "<otherwise> 1=1 </otherwise>",
                "</choose>",
                "<if test=\" lType != 'ALL' \">",
                    " AND L.TYPE = #{lType} ",
                "</if>",
                "<if test=\" sDate != null and !sDate.equals('') and eDate != null and !eDate.equals('') \">",
                    " AND M.REGIST_TIME BETWEEN TO_TIMESTAMP(#{sDate}, 'YYYY-MM-DD') AND TO_TIMESTAMP(#{eDate}, 'YYYY-MM-DD')",
                "</if>",
            "</where>",
            "ORDER BY ",
            "<choose>",
                "<when test='ob == 1'>L.ID ASC</when>",
                "<when test='ob == 2'>L.NAME ASC</when>",
                "<when test='ob == 3'>L.REGIST_TIME DESC</when>",
                "<otherwise>L.ID DESC</otherwise>",
            "</choose>",
        "</script>"
    })
    List<LayerEntity> findLayerEntitiesByPaginationModel(LayerSearchModel layerSearchModel);

    @Select({
        "<script>",
            "SELECT * FROM _MAP M ",
            "<where>",
                "<choose>",
                    "<when test='sb == 1'> M.NAME LIKE CONCAT('%', #{st}, '%') </when>",
                    "<when test='sb == 2'> M.REGISTOR_ID LIKE CONCAT('%', #{st}, '%') </when>",
                    "<when test='sb == 3'> LOWER(M.PROJECTION) LIKE CONCAT('%', #{st}, '%') </when>",
                    "<otherwise> 1=1 </otherwise>",
                "</choose>",
                "<if test=\" iType != 'ALL' \">",
                    " AND M.IMAGE_TYPE = #{iType}",
                "</if>",
                "<if test=\" units != 'ALL' \">",
                    " AND M.UNITS = #{units}",
                "</if>",
                "<if test=\" sDate != null and !sDate.equals('') and eDate != null and !eDate.equals('') \">",
                    " AND M.REGIST_TIME BETWEEN TO_TIMESTAMP(#{sDate}, 'YYYY-MM-DD') AND TO_TIMESTAMP(#{eDate}, 'YYYY-MM-DD')",
                "</if>",
            "</where>",
            "ORDER BY ",
            "<choose>",
                "<when test='ob == 1'>M.ID ASC</when>",
                "<when test='ob == 2'>M.NAME ASC</when>",
                "<when test='ob == 3'>M.REGIST_TIME DESC</when>",
                "<otherwise>M.ID DESC</otherwise>",
            "</choose>",
        "</script>"
    })
    List<MapEntity> findMapEntitiesByPaginationModel(MapSearchModel mapSearchModel);

    @Select("SELECT L.* FROM _MAP_LAYER_RELATION ML LEFT JOIN _LAYER L ON ML.LAYER_ID = L.ID WHERE ML.MAP_ID = #{mapId}")
    List<LayerEntity> findLayerEntitiesByMapId(@Param("mapId") Long mapId);

    //    @Select("SELECT S.* FROM _MAP_SOURCE S INNER JOIN _MAP_LAYER_SOURCE LS ON S.ID = LS.SOURCE_ID WHERE LS.LAYER_ID = #{layerId}")
    //    List<Map<String, Object>> getSourceListByLayerId(@Param("layerId") Long layerId);
    //
    //    @Select("SELECT COUNT(1) FROM _MAP_LAYER L INNER JOIN _MAP_LAYER_SOURCE LS ON L.ID = LS.LAYER_ID WHERE LS.SOURCE_ID = #{sourceId}")
    //    long getLayerCountBySourceId(@Param("sourceId") Long sourceId);

    @Delete("DELETE FROM _MAP_LAYER_RELATION WHERE MAP_ID = #{mapId}")
    void deleteLayerRelationsByMapId(@Param("mapId") Long mapId);
}
