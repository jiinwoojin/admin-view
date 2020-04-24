package com.jiin.admin.website.server.mapper;

import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@BaseMapper
public interface CountMapper {
    @Select("SELECT COUNT(1) FROM ${tableName}")
    long countByTableName(@Param("tableName") String tableName);

    @Select("SELECT COUNT(1) FROM ${tableName} WHERE SELECTED = TRUE")
    long countByProxyDataSelectedName(@Param("tableName") String tableName);

    @Select("SELECT COUNT(1) FROM _LAYER WHERE TYPE = #{type}")
    long countLayersByType(@Param("type") String type);

    @Select("SELECT COUNT(1) FROM _ACCOUNT WHERE ROLE_ID = #{roleId}")
    long countAccountsByRoleId(@Param("roleId") long roleId);
}
