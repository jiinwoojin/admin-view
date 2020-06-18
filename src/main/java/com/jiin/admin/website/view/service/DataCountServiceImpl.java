package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.*;
import com.jiin.admin.mapper.data.CountMapper;
import com.jiin.admin.vo.DataCounter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;

@Service
public class DataCountServiceImpl implements DataCountService {
    @Resource
    private CountMapper countMapper;

    /**
     * 대시보드 화면에 데이터 카운트 결과를 반환한다.
     * @param
     */
    public DataCounter loadDataCounter(){
        DataCounter dataCounter = new DataCounter();
        dataCounter.setMapCount(countMapper.countByTableName(MapEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSymbolCount(countMapper.countByTableName(MapSymbol.class.getAnnotation(Entity.class).name()));
        dataCounter.setRasterLayerCount(countMapper.countLayersByType("RASTER"));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("VECTOR"));
        dataCounter.setCadrgLayerCount(countMapper.countLayersByType("CADRG"));
        dataCounter.setLayersProxyCount(countMapper.countByTableName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesProxyCount(countMapper.countByTableName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesProxyCount(countMapper.countByTableName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setLayersSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));

        return dataCounter;
    }
}
