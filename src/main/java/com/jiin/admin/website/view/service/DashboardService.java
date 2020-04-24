package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.*;
import com.jiin.admin.website.server.mapper.CountMapper;
import com.jiin.admin.website.server.vo.DataCounter;
import com.jiin.admin.website.view.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Resource
    private CountMapper countMapper;

    @Resource
    private AccountMapper accountMapper;

    public DataCounter getDataCounter(){
        DataCounter dataCounter = new DataCounter();
        dataCounter.setMapCount(countMapper.countByTableName(MapEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSymbolCount(countMapper.countByTableName(MapSymbol.class.getAnnotation(Entity.class).name()));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("VECTOR"));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("RASTER"));
        dataCounter.setLayersProxyCount(countMapper.countByTableName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesProxyCount(countMapper.countByTableName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesProxyCount(countMapper.countByTableName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setLayersSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));

        Map<String, Long> accountMap = new HashMap<>();
        List<RoleEntity> roles = accountMapper.findAllRoles();
        roles.stream().forEach(r -> accountMap.put(r.getLabel(), countMapper.countAccountsByRoleId(r.getId())));
        dataCounter.setUserCount(accountMap);

        return dataCounter;
    }
}
