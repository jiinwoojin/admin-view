package com.jiin.admin.mapper.data;

import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.model.RelationModel;
import org.apache.ibatis.annotations.Param;

@BaseMapper
public interface ProxyCacheSourceRelationMapper {
    int insertByRelationModel(RelationModel relationModel);
    int deleteByCacheId(@Param("cacheId") long cacheId);
    int deleteBySourceId(@Param("sourceId") long sourceId);
}
