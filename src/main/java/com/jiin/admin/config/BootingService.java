package com.jiin.admin.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.dto.*;
import com.jiin.admin.entity.RoleEntity;
import com.jiin.admin.entity.SymbolPositionEntity;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.RelationModel;
import com.jiin.admin.website.model.SymbolPositionModel;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapProxyUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import com.jiin.admin.website.view.mapper.AccountMapper;
import com.jiin.admin.website.view.mapper.SymbolMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ComponentScan(basePackages = {"com.jiin.admin.website.gis"})
public class BootingService {
    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    File defaultMapProxy;

    @Value("classpath:data/default-layer.lay")
    private File defaultLayer;

    @Value("classpath:data/default-map.map")
    private File defaultMap;

    @Value("${project.server-port.mapserver-port}")
    private int MAP_SERVER_PORT;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private MapMapper mapMapper;

    @Resource
    private MapLayerRelationMapper mapLayerRelationMapper;

    @Resource
    private SymbolImageMapper symbolImageMapper;

    @Resource
    private SymbolPositionMapper symbolPositionMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    @Resource
    private ProxyCacheSourceRelationMapper proxyCacheSourceRelationMapper;

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxyLayerCacheRelationMapper proxyLayerCacheRelationMapper;

    @Resource
    private ProxyGlobalMapper proxyGlobalMapper;

    @Resource
    private CheckMapper checkMapper;

    @Resource
    private AccountMapper accountMapper;

    private String DEFAULT_DATA_NAME = "world";

    private String DEFAULT_MIDDLE_PATH = "NE2";

    private String DEFAULT_FILE_NAME = "NE2_HR_LC_SR_W_DR.tif";

    private String DEFAULT_SYMBOL_FOLDER = "GSymbol";

    private String DEFAULT_SYMBOL_NAME = "GSSSymbol";

    @Transactional
    // 지도 데이터인 NATURAL EARTH 2 TIFF 파일을 디폴트 값으로 넣어준다. (LAYER -> MAP -> CACHE)
    public void initializeMapData(){
        // LAYER INITIALIZE
        if (layerMapper.findByName(DEFAULT_DATA_NAME) == null) {
            String rasterDataPath = String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, DEFAULT_MIDDLE_PATH, DEFAULT_FILE_NAME);
            String layFilePath = String.format("%s%s/%s%s", dataPath, Constants.LAY_FILE_PATH, DEFAULT_DATA_NAME, Constants.LAY_SUFFIX);

            LayerDTO layerDTO = new LayerDTO(0L, DEFAULT_DATA_NAME, DEFAULT_DATA_NAME, Constants.EPSG_4326.toLowerCase(), DEFAULT_MIDDLE_PATH, "RASTER");
            layerDTO.setLayerFilePath(layFilePath.replace(dataPath, ""));
            layerDTO.setDataFilePath(rasterDataPath.replace(dataPath, ""));
            layerDTO.setDefault(true);

            layerDTO.setRegistorId("admin");
            layerDTO.setRegistorName("관리자");
            layerDTO.setRegistTime(new Date());
            layerDTO.setVersion(Double.parseDouble(String.format("%.1f", Constants.DEFAULT_LAYER_VERSION)));

            if (layerMapper.insert(layerDTO) > 0) {
                try {
                    String fileContext = MapServerUtil.fetchLayerFileContextWithDTO(defaultLayer, dataPath, layerDTO);
                    FileSystemUtil.createAtFile(layFilePath, fileContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // MAP INITIALIZE
        if (mapMapper.findByName(DEFAULT_DATA_NAME) == null) {
            String mapFilePath = String.format("%s%s/%s%s", dataPath, Constants.MAP_FILE_PATH, DEFAULT_DATA_NAME, Constants.MAP_SUFFIX);

            MapDTO mapDTO = new MapDTO();
            mapDTO.setId(mapMapper.findNextSeqVal());
            mapDTO.setName(DEFAULT_DATA_NAME);
            mapDTO.setDescription(DEFAULT_DATA_NAME);
            mapDTO.setMapFilePath(mapFilePath.replaceAll(dataPath, ""));
            mapDTO.setMinX("-180");
            mapDTO.setMinY("-90");
            mapDTO.setMaxX("180");
            mapDTO.setMaxY("90");
            mapDTO.setUnits("METERS");
            mapDTO.setProjection(Constants.EPSG_4326.toLowerCase());
            mapDTO.setImageType("png");
            mapDTO.setDefault(true);
            mapDTO.setRegistorId("admin");
            mapDTO.setRegistorName("관리자");
            mapDTO.setRegistTime(new Date());

            LayerDTO layer = layerMapper.findByName(DEFAULT_DATA_NAME);
            if (mapMapper.insert(mapDTO) > 0 && layer != null) {
                mapLayerRelationMapper.insert(
                    new MapLayerRelationDTO(0L, mapDTO.getId(), layer.getId(), 1)
                );

                String fileContext = null;
                try {
                    fileContext = MapServerUtil.fetchMapFileContextWithDTO(defaultMap, dataPath, mapDTO, Arrays.asList(layer));
                    FileSystemUtil.createAtFile(mapFilePath, fileContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // PROXY SOURCE INITIALIZE
        if (proxySourceMapper.findByName(DEFAULT_DATA_NAME) == null) {
            String mapFilePath = String.format("%s%s/%s%s", dataPath, Constants.MAP_FILE_PATH, DEFAULT_DATA_NAME, Constants.MAP_SUFFIX);
            long nextIdx = proxySourceMapper.findNextSeqVal();
            ProxySourceWMSDTO source = new ProxySourceWMSDTO();
            source.setId(nextIdx);
            source.setName(DEFAULT_DATA_NAME);
            source.setType("wms");
            source.setSelected(true);
            source.setIsDefault(true);
            source.setConcurrentRequests(4);
            source.setHttpClientTimeout(600);
            source.setWmsOptsVersion("1.3.0");
            source.setRequestURL(Constants.MAP_SERVER_WMS_URL);
            source.setRequestMap(mapFilePath);
            source.setRequestLayers(DEFAULT_DATA_NAME);
            source.setRequestTransparent(true);
            source.setSupportedSRS(String.format("%s,%s,%s", Constants.EPSG_4326, Constants.EPSG_3857, Constants.EPSG_900913));
            proxySourceMapper.insert(source);
            proxySourceMapper.insertWMS(source);
        }

        // PROXY CACHE INITIALIZE
        final String DEFAULT_CACHE_NAME = DEFAULT_DATA_NAME + "_cache";
        if (proxyCacheMapper.findByName(DEFAULT_CACHE_NAME) == null) {
            long nextIdx = proxyCacheMapper.findNextSeqVal();
            ProxyCacheDTO cache = new ProxyCacheDTO();
            cache.setId(nextIdx);
            cache.setName(DEFAULT_CACHE_NAME);
            cache.setFormat("image/png");
            cache.setGrids("GLOBAL_GEODETIC");
            cache.setSelected(true);
            cache.setIsDefault(true);

            if (proxyCacheMapper.insert(cache) > 0) {
                ProxySourceDTO source = proxySourceMapper.findByName(DEFAULT_DATA_NAME);
                if (source != null) {
                    proxyCacheSourceRelationMapper.insertByRelationModel(
                        new RelationModel(0L, nextIdx, source.getId())
                    );
                }
            }
        }

        // PROXY LAYER INITIALIZE
        final String DEFAULT_LAYER_NAME = DEFAULT_DATA_NAME + "_layer";
        if (proxyLayerMapper.findByName(DEFAULT_LAYER_NAME) == null) {
            long nextIdx = proxyLayerMapper.findNextSeqVal();
            ProxyLayerDTO layer = new ProxyLayerDTO();
            layer.setId(nextIdx);
            layer.setName(DEFAULT_LAYER_NAME);
            layer.setTitle(DEFAULT_DATA_NAME);
            layer.setSelected(true);
            layer.setIsDefault(true);

            if (proxyLayerMapper.insert(layer) > 0) {
                ProxyCacheDTO cache = proxyCacheMapper.findByName(DEFAULT_CACHE_NAME);
                if (cache != null) {
                    proxyLayerCacheRelationMapper.insertByRelationModel(
                            new RelationModel(0L, nextIdx, cache.getId())
                    );
                }
            }
        }

        // PROXY GLOBAL INITIALIZE
        final ProxyGlobalDTO cacheBase = new ProxyGlobalDTO(0L, "cache.base_dir", dataPath + "/cache");
        final ProxyGlobalDTO cacheLock = new ProxyGlobalDTO(0L, "cache.lock_dir", dataPath + "/cache/locks");
        for(ProxyGlobalDTO global : Arrays.asList(cacheBase, cacheLock)){
            if (proxyGlobalMapper.findByKey(global.getKey()) == null) {
                long nextIdx = proxyGlobalMapper.findNextSeqVal();
                global.setId(nextIdx);
                proxyGlobalMapper.insert(global);
            }
        }

        // MAP PROXY YAML FILE INITIALIZE
        List<ProxySourceMapServerDTO> mapServerDTOs = proxySourceMapper.findBySelectedMapServer(true);
        List<ProxySourceWMSDTO> wmsDTOs = proxySourceMapper.findBySelectedWMS(true);
        List<Object> sources = Stream.of(mapServerDTOs, wmsDTOs).flatMap(o -> o.stream()).collect(Collectors.toList());

        ServerCenterInfo localData;
        File file = Paths.get(dataPath, Constants.SERVER_INFO_FILE_PATH, Constants.SERVER_INFO_FILE_NAME).toFile();
        try {
            Map<String, Object> map = YAMLFileUtil.fetchMapByYAMLFile(file);
            Map<String, Object> local = (Map<String, Object>) map.get("local");
            localData = ServerCenterInfo.convertDTO(String.format("%s-%s-%s", local.get("zone"), local.get("kind"), local.get("name")), local);

            String context = MapProxyUtil.fetchYamlFileContextWithDTO(proxyLayerMapper.findBySelected(true), sources, proxyCacheMapper.findBySelected(true), proxyGlobalMapper.findAll(), localData, MAP_SERVER_PORT);
            FileSystemUtil.createAtFile(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void initializeSymbol() throws IOException {
        String mainPath = dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, DEFAULT_SYMBOL_FOLDER);
        String jsonPath = mainPath + String.format("/%s%s", DEFAULT_SYMBOL_NAME, Constants.JSON_SUFFIX);
        String json2xPath = mainPath + String.format("/%s%s", DEFAULT_DATA_NAME, Constants.JSON_2X_SUFFIX);
        String pngPath = mainPath + String.format("/%s%s", DEFAULT_SYMBOL_NAME, Constants.PNG_SUFFIX);
        String png2xPath = mainPath + String.format("/%s%s", DEFAULT_SYMBOL_NAME, Constants.PNG_2X_SUFFIX);

        SymbolImageDTO imageDTO = symbolImageMapper.findByName(DEFAULT_SYMBOL_NAME);
        if (imageDTO == null) {
            long idx = symbolImageMapper.findNextSeqVal();
            imageDTO = new SymbolImageDTO(idx, DEFAULT_SYMBOL_NAME, DEFAULT_SYMBOL_NAME, new Date(), "admin", "admin", true);
            imageDTO.setJsonFilePath(jsonPath.replace(dataPath, ""));
            imageDTO.setJson2xFilePath(json2xPath.replace(dataPath, ""));
            imageDTO.setImageFilePath(pngPath.replace(dataPath, ""));
            imageDTO.setImage2xFilePath(png2xPath.replace(dataPath, ""));

            symbolImageMapper.insert(imageDTO);
        }

        File jsonFile = new File(jsonPath);
        File imageFile = new File(pngPath);

        if (!jsonFile.exists() || !imageFile.exists()) return;

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(jsonFile, new TypeReference<Map<String, Object>>() {});
        BufferedImage image = ImageIO.read(imageFile);

        long cnt = symbolPositionMapper.countByImageId(imageDTO.getId());
        if (cnt == data.keySet().size()) return;

        for (String key : data.keySet()) {
            Map<String, Integer> map = (Map<String, Integer>) data.get(key);

            Integer height = map.getOrDefault("height", -1);
            Integer width = map.getOrDefault("width", -1);
            Integer pixelRatio = map.getOrDefault("pixelRatio", -1);
            Integer xPos = map.getOrDefault("x", -1);
            Integer yPos = map.getOrDefault("y", -1);

            if (height < 0 || width < 0 || pixelRatio < 0 || xPos < 0 || yPos < 0) continue;

            SymbolPositionDTO positionDTO = symbolPositionMapper.findByNameAndImageId(key, imageDTO.getId());
            if (positionDTO == null) {
                BufferedImage partition = image.getSubimage(xPos, yPos, width, height);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(partition, "PNG", baos);
                } catch (IOException e) {
                    System.out.println("ERROR - " + e.getMessage());
                }

                positionDTO = new SymbolPositionDTO(0L, key, height, width, pixelRatio, xPos, yPos, imageDTO.getId(), baos.toByteArray());
                symbolPositionMapper.insert(positionDTO);
            }
        }
    }

    @Transactional
    public void initializeAccounts() {
        if (checkMapper.countDuplicateAccount("admin") < 1) {
            RoleEntity adminRole = accountMapper.findRoleByTitle("ADMIN");
            accountMapper.insertAccount(new AccountDTO(null, "admin", "10e090c7f6089da649cd9649052fcfa9e8bdd1a73734453334cb98fcdde0", "관리자", "admin@ji-in.com", adminRole.getId()));
        }
        if (checkMapper.countDuplicateAccount("user") < 1) {
            RoleEntity userRole = accountMapper.findRoleByTitle("USER");
            accountMapper.insertAccount(new AccountDTO(null, "user", "10e090c7f6089da649cd9649052fcfa9e8bdd1a73734453334cb98fcdde0", "사용자", "user@ji-in.com", userRole.getId()));
        }
    }


    @Transactional
    public void initializeRoles() {
        if (checkMapper.countDuplicateRole("ADMIN") < 1) {
            accountMapper.insertRole(new RoleEntity(null, "ADMIN", "관리자", true, true, true, true, true));
        }
        if (checkMapper.countDuplicateRole("USER") < 1) {
            accountMapper.insertRole(new RoleEntity(null, "USER", "일반 사용자", false, false, false, false, false));
        }
    }
}
