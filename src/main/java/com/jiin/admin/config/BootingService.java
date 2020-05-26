package com.jiin.admin.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.dto.AccountDTO;
import com.jiin.admin.dto.ServicePortV2DTO;
import com.jiin.admin.entity.*;
import com.jiin.admin.entity.enumeration.ServerType;
import com.jiin.admin.entity.ServicePortEntity;
import com.jiin.admin.website.model.ServerConnectionModel;
import com.jiin.admin.website.model.ServerRelationModel;
import com.jiin.admin.website.model.ServicePortModel;
import com.jiin.admin.website.model.SymbolPositionModel;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.view.mapper.AccountMapper;
import com.jiin.admin.website.view.mapper.ServiceMapper;
import com.jiin.admin.website.view.mapper.SymbolMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
@ComponentScan(basePackages = {"com.jiin.admin.website.gis"})
public class BootingService {
    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    File defaultMapProxy;

    @Resource
    DockerService dockerService;

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    private SymbolMapper symbolMapper;

    @Resource
    private CheckMapper checkMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private ServiceMapper serviceMapper;

//    @Resource
//    private ManageMapper manageMapper;
//
//    @Resource
//    private ProxyMapper cacheMapper;
//
//    @Resource
//    private MapProxyYamlComponent mapProxyYamlComponent;

    @Transactional
    public void initializeSymbol() throws IOException {
        System.out.println(">>> initializeSymbol Start");

        String path = dataPath + "/html/GSymbol";
        String jsonDir = path + "/GSSSymbol.json";

        ObjectMapper mapper = new ObjectMapper();

        File file = new File(jsonDir);

        if(!file.exists()) return;

        Map<String, Object> data = mapper.readValue(file, new TypeReference<Map<String, Object>>() {});
        for(String key : data.keySet()){
            Map<String, Integer> map = (Map<String, Integer>) data.get(key);

            Integer height = map.getOrDefault("height", -1);
            Integer width = map.getOrDefault("width", -1);
            Integer pixelRatio = map.getOrDefault("pixelRatio", -1);
            Integer xPos = map.getOrDefault("x", -1);
            Integer yPos = map.getOrDefault("y", -1);

            if(height < 0 || width < 0 || pixelRatio < 0 || xPos < 0 || yPos < 0) continue;

            SymbolPositionEntity entity = symbolMapper.findPositionBySymbolName(key);
            if(entity == null) {
                symbolMapper.insertWithSymbolPositionModel(
                    new SymbolPositionModel(0L, key, height, width, pixelRatio, xPos, yPos)
                );
            }
        }

//        entityManager.createQuery("DELETE FROM " + MapSymbol.class.getAnnotation(Entity.class).name()).executeUpdate();
//        int i = 0;
//        while(i++ < 10){
//            MapSymbol symbol = new MapSymbol();
//            symbol.setName("테스트");
//            symbol.setCode("SYMBOL" + i);
//            symbol.setType("POLYGON");
//            entityManager.persist(symbol);
//        }
    }

    @Transactional
    public void initializeLayer() throws IOException {
        // Default Map Proxy Setting

        // if yaml file is not existing or has no-value

        // 1. Map Entity Reload

        // 2. Source Entity Reload

        // 3. Map Proxy Model Selected Model Mapping & Saving

        // 4. YAML File Re-Saving

        // 5. Dockerfile Restart

        // 기본 레이어 및 소스 초기화
        System.out.println(">>> initializeLayer Start");
        Date now = new Date();
        entityManager.createQuery("DELETE FROM " + MapEntity.class.getAnnotation(Entity.class).name() + " WHERE IS_DEFAULT = true").executeUpdate();
        entityManager.createQuery("DELETE FROM " + LayerEntity.class.getAnnotation(Entity.class).name()+ " WHERE IS_DEFAULT = true").executeUpdate();
        /*YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Map mapproxy = mapper.readValue(defaultMapProxy, Map.class);
        List<Map> layers = (List<Map>) mapproxy.get("layers");
        Map caches = (Map) mapproxy.get("caches");
        Map sources = (Map) mapproxy.get("sources");
        Map<String,MapSource> entity = new HashMap();
        for(Object key : sources.keySet()){
            Map sourceMap = (Map) sources.get(key);
            Map sourceReqMap = (Map) sourceMap.get("req");
            MapSource source = new MapSource();
            source.setDefault(true); // default
            source.setName((String) key);
            source.setType((String) sourceMap.get("type"));
            if(sourceReqMap != null){
                source.setMapPath((String) sourceReqMap.get("map"));
                source.setLayers("world"); // TODO : 추출작업 필요
            }
            for(Object cacheKey : caches.keySet()){
                Map cacheMap = (Map) caches.get(cacheKey);
                List<String> cacheSources = (List<String>) cacheMap.get("sources");
                if(cacheSources.contains(key)){
                    source.setUseCache(true);
                    source.setCacheName((String) cacheKey);
                    break;
                }
            }
            source.setRegistorId("system");
            source.setRegistorName("system");
            source.setRegistTime(now);
            entityManager.persist(source);
            entity.put(source.isUseCache() ? source.getCacheName() : source.getName(),source);
        }
        for(Map o : layers){
            MapLayer layer = new MapLayer();
            layer.setDefault(true); // default
            layer.setName((String) o.get("name"));
            layer.setTitle((String) o.get("title"));
            List<String> sourceStrs = (List) o.get("sources");
            List<MapSource> sourceEntity = new ArrayList<>();
            for(String sourceStr : sourceStrs){
                sourceEntity.add(entity.get(sourceStr));
            }
            //layer.setSource(sourceEntity);
            layer.setRegistorId("system");
            layer.setRegistorName("system");
            layer.setRegistTime(now);
            entityManager.persist(layer);
        }*/

        // mapproxy write
        dockerService.proxyReloadFromDatabase();
    }

    @Transactional
    public void initializeCache() throws IOException {
        // Map Proxy 기본 데이터 저장을 위한 로직 (미완성)
        // Map Server 에서 MAP 데이터 중 isDefault 가 true 인 데이터들만 YAML 파일에 초기화 하는 로직.
        /*
        Map<String, Object> settingMap = mapProxyYamlComponent.getMapProxyYamlFileMapObject();
        if(settingMap != null){
            List<Map<String, Object>> layers = (ArrayList<Map<String, Object>>) settingMap.getOrDefault("layers", null);
            Map<String, Object> sources = (Map<String, Object>) settingMap.getOrDefault("sources", null);

            if(layers == null || sources == null) return;

            // MapProxy sources 데이터 초기화
            for(String key : sources.keySet()){
                Map<String, Object> source = (Map<String, Object>) sources.getOrDefault(key, null);

                if(source != null){
                    MapEntity mapEntity = manageMapper.findMapEntityByName(key);
                    List<LayerEntity> layerEntities = manageMapper.findLayerEntitiesByMapId(mapEntity.getId());

                    if(mapEntity != null) {
                        ProxySourceModel model = new ProxySourceModel(0L, key, "mapserver", dataPath + mapEntity.getMapFilePath(), null, mapServerBinary, dataPath);
                        List<String> defaultLayer = new ArrayList<>();
                        for(LayerEntity layer : layerEntities){
                            if(layer.isDefault()){
                                defaultLayer.add(layer.getName());
                            }
                        }

                        if(defaultLayer.size() > 0) {
                            model.setRequestLayers(defaultLayer.stream().collect(Collectors.joining()));
                            if (checkMapper.countDuplicate(ProxySourceEntity.class.getAnnotation(Entity.class).name(), key) > 0) {
                                cacheMapper.updateProxySourceInitWithModel(model);
                            } else {
                                cacheMapper.insertProxySourceInitWithModel(model);
                            }
                        } else {
                            System.out.println("Map File Has Not Default Layer! >> " + source);
                        }
                    } else {
                        System.out.println("Map Server Data is Non Existed! >> " + source);
                    }
                }
            }
        }
        */
    }

    @Transactional
    public void initializeAccounts(){
        if(checkMapper.countDuplicateAccount("admin") < 1){
            RoleEntity adminRole = accountMapper.findRoleByTitle("ADMIN");
            accountMapper.insertAccount(new AccountDTO(null, "admin", "10e090c7f6089da649cd9649052fcfa9e8bdd1a73734453334cb98fcdde0", "관리자", "admin@ji-in.com", adminRole.getId()));
        }
        if(checkMapper.countDuplicateAccount("user") < 1){
            RoleEntity userRole = accountMapper.findRoleByTitle("USER");
            accountMapper.insertAccount(new AccountDTO(null, "user", "10e090c7f6089da649cd9649052fcfa9e8bdd1a73734453334cb98fcdde0", "사용자", "user@ji-in.com", userRole.getId()));
        }
    }


    @Transactional
    public void initializeRoles(){
        if(checkMapper.countDuplicateRole("ADMIN") < 1){
            accountMapper.insertRole(new RoleEntity(null, "ADMIN", "관리자", true, true, true, true, true));
        }
        if(checkMapper.countDuplicateRole("USER") < 1){
            accountMapper.insertRole(new RoleEntity(null, "USER", "일반 사용자", false, false, false, false, false));
        }
    }

    @Transactional
    public void initializeServerConnections(){
        //serviceMapper.truncateTableWithName("_SERVICE_PORT");
        //serviceMapper.truncateTableWithName("_SERVER_RELATION");
        //serviceMapper.truncateTableWithName("_SERVER_CONNECTION");

        // 서버 정보 초기화 로직 : 파일 호출 이후 올바른 정보로 대체할 필요는 있음.
        final List<String> KEY_LIST = Arrays.asList("B1-Svr1", "B1-Svr2", "NB1-Svr1", "NB1-Svr2", "U3-Svr1", "U3-Svr2", "NU3-Svr1", "NU3-Svr2", "GOC-Svr1", "GOC-Svr2", "B1-CDS", "U3-CDS", "B1-Test1", "B1-Test2");
        for(String key : KEY_LIST){
            ServerConnectionModel model = null;
            System.out.println(key);
            switch(key){
                case "B1-Svr1" :
                    model = new ServerConnectionModel(0L, "B1-Svr1", "B1 SI Server 1", ServerType.SI.name(), "192.168.1.141", "11110", "jiapp", "jiin0701!");
                    break;
                case "B1-Svr2" :
                    model = new ServerConnectionModel(0L, "B1-Svr2", "B1 SI Server 2", ServerType.SI.name(), "192.168.1.142", "11110", "jiapp", "jiin0701!");
                    break;
                case "NB1-Svr1" :
                    model = new ServerConnectionModel(0L, "NB1-Svr1", "B1 N-SI Server 1", ServerType.N_SI.name(), "192.168.1.152", "11110", "jiapp", "jiin0701!");
                    break;
                case "NB1-Svr2" :
                    model = new ServerConnectionModel(0L, "NB1-Svr2", "B1 N-SI Server 2", ServerType.N_SI.name(), "192.168.1.153", "11110", "jiapp", "jiin0701!");
                    break;
                case "U3-Svr1" :
                    model = new ServerConnectionModel(0L, "U3-Svr1", "U3 SI Server 1", ServerType.SI.name(), "192.168.1.155", "11110", "jiapp", "jiin0701!");
                    break;
                case "U3-Svr2" :
                    model = new ServerConnectionModel(0L, "U3-Svr2", "U3 SI Server 2", ServerType.SI.name(), "192.168.1.156", "11110", "jiapp", "jiin0701!");
                    break;
                case "NU3-Svr1" :
                    model = new ServerConnectionModel(0L, "NU3-Svr1", "U3 N-SI Server 1", ServerType.N_SI.name(), "192.168.1.158", "11110", "jiapp", "jiin0701!");
                    break;
                case "NU3-Svr2" :
                    model = new ServerConnectionModel(0L, "NU3-Svr2", "U3 N-SI Server 2", ServerType.N_SI.name(), "192.168.1.159", "11110", "jiapp", "jiin0701!");
                    break;
                case "GOC-Svr1" :
                    model = new ServerConnectionModel(0L, "GOC-Svr1", "GOC SI Server 1", ServerType.SI.name(), "192.168.1.161", "11110", "jiapp", "jiin0701!");
                    break;
                case "GOC-Svr2" :
                    model = new ServerConnectionModel(0L, "GOC-Svr2", "GOC SI Server 2", ServerType.SI.name(), "192.168.1.162", "11110", "jiapp", "jiin0701!");
                    break;
                case "B1-CDS" :
                    model = new ServerConnectionModel(0L, "B1-CDS", "B1 CDS", ServerType.CDS.name(), "192.168.1.164", "11110", "jiapp", "jiin0701!");
                    break;
                case "U3-CDS" :
                    model = new ServerConnectionModel(0L, "U3-CDS", "U3 CDS", ServerType.CDS.name(), "192.168.1.165", "11110", "jiapp", "jiin0701!");
                    break;
                case "B1-Test1" : // 테스트 데이터
                    model = new ServerConnectionModel(0L, "B1-Test1", "B1 Test 1", ServerType.SI.name(), "192.168.2.51", "11110", "root", "jiin0701!");
                    break;
                case "B1-Test2" : // 테스트 데이터
                    model = new ServerConnectionModel(0L, "B1-Test2", "B1 Test 2", ServerType.SI.name(), "192.168.2.157", "8090", "root", "jiin0701!");
                    break;
            }
            if(serviceMapper.findServerConnectionByName(key) == null) serviceMapper.insertServerConnectionWithModel(model);
            else serviceMapper.updateServerConnectionWithModel(model);
        }

        // 서비스 포트 초기화 로직
        Map<String, ServerConnectionEntity> map = new HashMap<>();
        for (String key : KEY_LIST) {
            ServerConnectionEntity entity = serviceMapper.findServerConnectionByName(key);
            map.put(key, entity);

            ServicePortEntity portEntity = serviceMapper.findPortConfigBySvrId(entity.getId());
            if(portEntity == null) {
                ServicePortModel port = new ServicePortModel();
                port.setSvrId(entity.getId());
                serviceMapper.insertServicePortWithModel(port);
            }

            ServicePortV2Entity portV2Entity = serviceMapper.findBySvrId(entity.getId());
            if(portV2Entity == null){
                ServicePortV2DTO port = new ServicePortV2DTO();
                port.setSvrId(entity.getId());
                serviceMapper.insertServicePortV2(port);
            }
        }

        // 서버 관계 초기화 로직 개선 : 관계 생성
        final Map<String, List<String>> relations = new HashMap<String, List<String>>(){{
            put("B1-Svr1", Arrays.asList("B1-Svr1", "B1-Svr2", "B1-CDS", "U3-Svr1", "U3-Svr2", "GOC-Svr1", "GOC-Svr2"));
            put("B1-Svr2", Arrays.asList("B1-Svr1", "B1-Svr2", "B1-CDS", "U3-Svr1", "U3-Svr2", "GOC-Svr1", "GOC-Svr2"));
            put("U3-Svr1", Arrays.asList("B1-Svr1", "B1-Svr2", "U3-Svr1", "U3-Svr2", "U3-CDS", "GOC-Svr1", "GOC-Svr2"));
            put("U3-Svr2", Arrays.asList("B1-Svr1", "B1-Svr2", "U3-Svr1", "U3-Svr2", "U3-CDS", "GOC-Svr1", "GOC-Svr2"));
            put("GOC-Svr1", Arrays.asList("B1-Svr1", "B1-Svr2", "U3-Svr1", "U3-Svr2", "GOC-Svr1", "GOC-Svr2"));
            put("GOC-Svr2", Arrays.asList("B1-Svr1", "B1-Svr2", "U3-Svr1", "U3-Svr2", "GOC-Svr1", "GOC-Svr2"));
            put("NB1-Svr1", Arrays.asList("NB1-Svr1", "NB1-Svr2", "B1-CDS", "NU3-Svr1", "NU3-Svr2"));
            put("NB1-Svr2", Arrays.asList("NB1-Svr1", "NB1-Svr2", "B1-CDS", "NU3-Svr1", "NU3-Svr2"));
            put("NU3-Svr1", Arrays.asList("NB1-Svr1", "NB1-Svr2", "NU3-Svr1", "NU3-Svr2", "U3-CDS"));
            put("NU3-Svr2", Arrays.asList("NB1-Svr1", "NB1-Svr2", "NU3-Svr1", "NU3-Svr2", "U3-CDS"));
            put("B1-CDS", Arrays.asList("B1-Svr1", "B1-Svr2", "NB1-Svr1", "NB1-Svr2"));
            put("U3-CDS", Arrays.asList("U3-Svr1", "U3-Svr2", "NU3-Svr1", "NU3-Svr2"));
            put("B1-Test1", Arrays.asList("B1-Test1", "B1-Test2", "U3-Svr1", "GOC-Svr1")); // 테스트 데이터
            put("B1-Test2", Arrays.asList("B1-Test1", "B1-Test2", "U3-Svr1", "GOC-Svr1")); // 테스트 데이터
        }};

        // 서버 관계 초기화 로직 개선 : 관계에 따른 데이터 추가
        for(String main : relations.keySet()){
            List<String> anos = relations.get(main);
            long mainSvrId = map.get(main).getId();
            for(String sub : anos){
                long subSvrId = map.get(sub).getId();
                if(serviceMapper.countByMainSvrIdAndSubSvrId(mainSvrId, subSvrId) == 0){
                    serviceMapper.insertServerRelationWithModel(
                        new ServerRelationModel(0L, mainSvrId, subSvrId)
                    );
                }
            }
        }
    }
}
