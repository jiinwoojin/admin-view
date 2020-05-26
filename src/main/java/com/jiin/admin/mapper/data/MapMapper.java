package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.MapPageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface MapMapper {
    List<MapDTO> findAll();
    List<MapDTO> findByPageModel(MapPageModel mapPageModel);

    MapDTO findByName(@Param("name") String name);
}
