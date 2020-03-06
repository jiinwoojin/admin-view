package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@BaseMapper
public interface ManageMapper {

    @Select("SELECT * FROM _MAP_LAYER")
    List<Map> getLayerList();

    @Select("SELECT * FROM _MAP_SOURCE")
    List<Map> getSourceList();
}
