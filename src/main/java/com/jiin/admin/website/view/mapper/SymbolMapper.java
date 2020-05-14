package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.entity.SymbolPositionEntity;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.SymbolPositionModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

@BaseMapper
public interface SymbolMapper {
    @Insert("INSERT INTO _SYMBOL_POSITION(ID, NAME, HEIGHT, WIDTH, PIXEL_RATIO, X_POS, Y_POS) VALUES(#{id}, #{name}, #{height}, #{width}, #{pixelRatio}, #{xPos}, #{yPos})")
    @SelectKey(statement="SELECT NEXTVAL('SYMBOL_POSITION_SEQ')", keyProperty="id", before=true, resultType=long.class)
    void insertWithSymbolPositionModel(SymbolPositionModel symbolPositionModel);

    @Select("SELECT * FROM _SYMBOL_POSITION WHERE NAME = #{name}")
    SymbolPositionEntity findPositionBySymbolName(@Param("name") String name);

    @Select({
        "<script>",
            "SELECT * FROM _SYMBOL_POSITION SP ",
            "<where>",
                " SP.NAME LIKE CONCAT('%', #{st}, '%') ",
            "</where>",
            " ORDER BY ",
            "<choose>",
                "<when test='ob == 1'>SP.NAME ASC</when>",
                "<when test='ob == 2'>SP.NAME DESC</when>",
                "<when test='ob == 3'>SP.X_POS ASC</when>",
                "<when test='ob == 4'>SP.X_POS DESC</when>",
                "<when test='ob == 5'>SP.Y_POS ASC</when>",
                "<when test='ob == 6'>SP.Y_POS DESC</when>",
                "<otherwise> SP.ID ASC </otherwise>",
            "</choose>",
        "</script>"
    })
    List<SymbolPositionEntity> findAllPositionsByPagination(@Param("st") String st, @Param("ob") int ob);

    @Select("SELECT * FROM _MAP_SYMBOL")
    List<MapSymbol> list();
}
