package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ContainerHistoryDTO;
import com.jiin.admin.mapper.BaseMapper;

import java.util.List;

@BaseMapper
public interface ContainerHistoryMapper {
    long findNextSeqVal();
    List<ContainerHistoryDTO> findAll();
    int insert(ContainerHistoryDTO containerHistoryDTO);
    int deleteAll();
}
