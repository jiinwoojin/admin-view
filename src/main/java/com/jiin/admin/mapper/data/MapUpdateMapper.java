package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface MapUpdateMapper {

    List<VersionDTO> findByNameAndVersion(@Param("name") String map, @Param("version") Double version);

    VersionDTO findFileInfo(@Param("name") String map, @Param("version") Double currentVersion);
}
