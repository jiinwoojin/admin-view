package com.jiin.admin.mapper.data;

import com.jiin.admin.dto.ProxySeedCronDTO;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

@BaseMapper
public interface ProxySeedCronMapper {
    ProxySeedCronDTO findById(@Param("id") long id);
    ProxySeedCronDTO findByCacheId(@Param("cacheId") long cacheId);
    int insert(ProxySeedCronDTO proxySeedCronDTO);
    int update(ProxySeedCronDTO proxySeedCronDTO);
    int deleteById(@Param("id") long id);
    int deleteByCacheId(@Param("cacheId") long cacheId);
}
