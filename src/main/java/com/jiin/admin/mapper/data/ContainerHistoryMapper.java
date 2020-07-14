package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ContainerHistoryDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@BaseMapper
public interface ContainerHistoryMapper {
    long findNextSeqVal();
    List<ContainerHistoryDTO> findAll();
    int insert(ContainerHistoryDTO containerHistoryDTO);
    int updateHostnameData(@Param("before") String before, @Param("after") String after);
    int deleteAll();
}
