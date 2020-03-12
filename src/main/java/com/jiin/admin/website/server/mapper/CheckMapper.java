package com.jiin.admin.website.server.mapper;

import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@BaseMapper
public interface CheckMapper {
    @Select("SELECT COUNT(1) FROM ${tableName} WHERE NAME = #{name}")
    int countDuplicate(@Param("tableName") String tableName, @Param("name") String name);
}
