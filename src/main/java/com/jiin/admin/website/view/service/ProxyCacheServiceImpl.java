package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.mapper.data.ProxyCacheMapper;
import com.jiin.admin.mapper.data.ProxyLayerMapper;
import com.jiin.admin.mapper.data.ProxySourceMapper;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
     * YAML 파일 내용을 채취한 이후, 해당 디렉토리에 저장힌다.
     * @param
     */
    private boolean saveYAMLFileByEachList(){
        String context;
        try {
            context = MapProxyUtil.fetchYamlFileContextWithDTO(
                proxyLayerMapper.findAll(), proxySourceMapper.findAll(), proxyCacheMapper.findAll(), dataPath
            );
        } catch (IOException e) {
            log.error("Map Proxy YAML 파일을 생성할 수 없습니다. : DB 내용 오류");
            return false;
        }

        try {
            FileSystemUtil.createAtFile(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME, context);
        } catch (IOException e) {
            log.error("Map Proxy YAML 파일을 생성할 수 없습니다. : 파일 형성 오류");
            return false;
        }

        return true;
    }

    /**
     * layers 와 sources 의 Relation 을 생성한다.
     * @param id long, sources List : String
     */
    private boolean createRelationWithLayerAndSources(long id, List<String> sources){
        return false;
    }

    /**
     * layers 와 caches 의 Relation 을 생성한다.
     * @param id long, caches List : String
     */
    private boolean createRelationWithLayerAndCaches(long id, List<String> caches){
        return false;
    }


    /**
     * cache 와 sources 의 Relation 을 생성한다.
     * @param id long, sources List : String
     */
    private boolean createRelationWithCacheAndSources(long id, List<String> sources){
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

    @Override
    public String loadMapServerBinary() {
        return mapServerBinary;
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

    /**
     * MapProxy 기반 Layer 정보를 저장한다.
     * @param proxyLayerModel ProxyLayerModel
     */
    @Override
    public boolean saveProxyLayerByModel(ProxyLayerModelV2 proxyLayerModelV2) {
        return false;
    }

    /**
     * MapProxy 기반 Source 정보를 저장한다.
     * @param proxySourceModel ProxySourceModel
     */
    @Override
    public boolean saveProxySourceByModel(ProxySourceModelV2 proxySourceModelV2) {
        return false;
    }

    /**
     * MapProxy 기반 Cache 정보를 저장한다.
     * @param proxyCacheModel ProxyCacheModel
     */
    @Override
    public boolean saveProxyCacheByModel(ProxyCacheModelV2 proxyCacheModelV2) {
        return false;
    }

    @Override
    public boolean removeProxyDataByIdAndType(long id, String type) {
        return false;
    }

    @Override
    public boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel) {
        return false;
    }
}
