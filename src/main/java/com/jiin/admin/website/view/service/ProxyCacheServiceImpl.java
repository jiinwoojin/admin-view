package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.mapper.data.ProxyCacheMapper;
import com.jiin.admin.mapper.data.ProxyLayerMapper;
import com.jiin.admin.mapper.data.ProxySourceMapper;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProxyCacheServiceImpl implements ProxyCacheService {
    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    /**
     * layers 와 sources 의 Relation 을 생성한다.
     * @param layer ProxyLayerDTO, sources List : ProxySourceDTO
     */
    private boolean createRelationWithLayerAndSources(ProxyLayerDTO layer, List<ProxySourceDTO> sources){
        return false;
    }

    /**
     * layers 와 caches 의 Relation 을 생성한다.
     * @param layer ProxyLayerDTO, caches List : ProxyCacheDTO
     */
    private boolean createRelationWithLayerAndCaches(ProxyLayerDTO layer, List<ProxyCacheDTO> caches){
        return false;
    }


    /**
     * cache 와 sources 의 Relation 을 생성한다.
     * @param cache ProxyCacheDTO, sources List : ProxySourceDTO
     */
    private boolean createRelationWithCacheAndSources(ProxyCacheDTO cache, List<ProxySourceDTO> sources){
        return false;
    }

    @Override
    public String loadDataDir() {
        return dataPath;
    }

    @Override
    public String loadProxyMainDir() {
        return dataPath + "/cache/";
    }

    /**
     * 각 캐시 정보 목록들을 불러온다.
     * @param type String
     */
    @Override
    public Object loadDataList(String type) {
        switch(type){
            case "LAYERS" :
                return proxyLayerMapper.findAll();
            case "SOURCES" :
                return proxySourceMapper.findAll();
            case "CACHES" :
                return proxyCacheMapper.findAll();
            default :
                return new ArrayList<>();
        }
    }

    /**
     * 각 캐시 정보 목록 중 설정에 선택 or 미선택 된 목록들을 불러온다.
     * @param type String
     */
    @Override
    public Object loadDataListBySelected(String type, Boolean selected) {
        switch(type){
            case "LAYERS" :
                return proxyLayerMapper.findBySelected(selected);
            case "SOURCES" :
                return proxySourceMapper.findBySelected(selected);
            case "CACHES" :
                return proxyCacheMapper.findBySelected(selected);
            default :
                return new ArrayList<>();
        }
    }

    /**
     * 각 캐시 정보의 Form 을 초기화 한다.
     * @param type String
     */
    @Override
    public Object loadDataModel(String type) {
        switch(type){
            case "LAYERS" :
                return new ProxyLayerModel(0L, "", "", new ArrayList<>(), new ArrayList<>());
            case "SOURCES" :
                return new ProxySourceModel(0L, "", "mapserver", "[none]", "[none]", mapServerBinary, dataPath + "/tmp");
            case "CACHES" :
                return new ProxyCacheModel(0L, "", "file", "", 0, 0, 0, new ArrayList<>());
            default :
                return null;
        }
    }

    /**
     * 현재 MapProxy 의 yaml 설정 파일에 선택된 데이터 목록을 호출한다.
     * @param
     */
    @Override
    public ProxySelectModel loadProxySetting() {
        return new ProxySelectModel(
              proxyLayerMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList()),
              proxySourceMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList()),
              proxyCacheMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList())
        );
    }

    @Override
    public boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel) {
        return false;
    }
}
