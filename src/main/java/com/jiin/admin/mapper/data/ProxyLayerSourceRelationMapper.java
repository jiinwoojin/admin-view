package com.jiin.admin.mapper.data;

import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.RelationModel;
import org.apache.ibatis.annotations.Param;

@BaseMapper
public interface ProxyLayerSourceRelationMapper {
    int insertByRelationModel(RelationModel relationModel);
    int deleteByLayerId(@Param("layerId") long layerId);
    int deleteBySourceId(@Param("sourceId") long sourceId);
}
