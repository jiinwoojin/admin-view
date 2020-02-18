package com.jiin.admin.website.server._default;

import com.jiin.admin.mapper.MapMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@MapMapper
public interface AdminServerDefaultMapper {

    @Select("SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3")
    List<Integer> testByClass();

    List<Integer> testByXml();
}
