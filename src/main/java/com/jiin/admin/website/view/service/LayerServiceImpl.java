package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapLayerRelationMapper;
import com.jiin.admin.mapper.data.MapVersionMapper;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.view.component.MapVersionManagement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class LayerServiceImpl implements LayerService {
    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-layer.lay")
    private File defaultLayer;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private MapLayerRelationMapper mapLayerRelationMapper;

    @Resource
    private MapVersionMapper mapVersionMapper;

    @Resource
    private MapVersionManagement mapVersionManagement;

    private static Double DEFAULT_LAYER_VERSION = 1.0;

    private static String CADRG_DEFAULT_EXECUTE_DIRECTORY = "/RPF";

    private static String CADRG_DEFAULT_EXECUTE_FILE = "/A.TOC";

    private static final List<OptionModel> sbOptions = Arrays.asList(
        new OptionModel("-- 검색 키워드 선택 --", 0),
        new OptionModel("레이어 이름", 1),
        new OptionModel("레이어 등록자", 2),
        new OptionModel("레이어 좌표 체계", 3)
    );

    private static final List<OptionModel> obOptions = Arrays.asList(
        new OptionModel("-- 정렬 방식 선택 --", 0),
        new OptionModel("ID 순서 정렬", 1),
        new OptionModel("이름 순서 정렬", 2),
        new OptionModel("등록 기간 역순 정렬", 3)
    );

    /**
     * 새로운 LAYER 업로드 파일을 로컬에 옮기고 처리하기
     * @param layerDTO LayerDTO, uploadFile MultipartFile
     */
    private void transferNewUploadFile(LayerDTO layerDTO, MultipartFile uploadFile){
        // 1단계. 레이어 리소스 파일 디렉토리 755 권한 설정
        String dataDirStr = dataPath + Constants.DATA_PATH + "/" + layerDTO.getMiddleFolder();
        File dataDir = new File(dataDirStr);
        if(!dataDir.exists()){
            if(dataDir.mkdirs() && !FileSystemUtil.isWindowOS()){
                try {
                    FileSystemUtil.setFileDefaultPermissions(dataDir.toPath());
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // 2단계. 레이어 리소스 파일 업로드 뒤 옮기기 (CADRG 아닌 버전 기준)
        String filename = uploadFile.getOriginalFilename();
        File dataFile = new File(String.format("%s/%s", dataDirStr, uploadFile.getOriginalFilename()));
        try {
            uploadFile.transferTo(dataFile);

            // 압축 파일인 경우 (CADRG 혹은 SHP) 에는 파일 압축 해제를 진행해야 한다.
            if(filename.contains(".zip") || filename.contains(".ZIP")){
                FileSystemUtil.decompressZipFile(dataFile);
                FileSystemUtil.deleteFile(dataFile.getPath()); // 파일 업로드가 완료되면 삭제한다.
            }
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * LAYER 리소스 파일을 삭제한다.
     * @param layerDTO LayerDTO
     */
    private void removeLayerResourceFile(LayerDTO layerDTO){
        String dataFilePath = dataPath + layerDTO.getDataFilePath();
        try {
            if(!layerDTO.getType().equals("CADRG")) {
                FileSystemUtil.deleteFile(dataFilePath);
            } else {
                FileSystemUtil.deleteFile(dataFilePath.replace(CADRG_DEFAULT_EXECUTE_DIRECTORY + CADRG_DEFAULT_EXECUTE_FILE, ""));
            }
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * LAYER 데이터 중 추가 및 삭제 시 공통으로 설정할 수 있는 로직 정리
     * @param layerDTO LayerDTO
     */
    private void setCommonProperties(LayerDTO layerDTO){
        // TODO : 굳이 setter 안 쓰고, Builder 를 사용해서 한 번에 실행하는 방법이 있을까? - Builder 패턴 알아볼 것
        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        layerDTO.setRegistorId(loginUser);
        layerDTO.setRegistorName(loginUser);
        layerDTO.setType(layerDTO.getType().toUpperCase());
    }

    /**
     * LAYER 검색 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadSearchByOptionList() {
        return sbOptions;
    }

    /**
     * LAYER 정렬 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadOrderByOptionList() {
        return obOptions;
    }

    /**
     * Layer 데이터 목록 조회 with Pagination Model
     * @param layerPageModel LayerPageModel
     */
    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(LayerPageModel layerPageModel) {
        return new HashMap<String, Object>(){{
            put("data", layerMapper.findByPageModel(layerPageModel));
            put("count", layerMapper.countByPageModel(layerPageModel));
        }};
    }

    /**
     * LAYER 모든 데이터 조회 - DataTables 에서 사용
     */
    @Override
    public List<LayerDTO> loadDataList() {
        return layerMapper.findAll();
    }

    /**
     * MAP 파일과 연관된 LAYER 파일 호출
     * @param mapId long
     */
    @Override
    public List<LayerDTO> loadDataListByMapId(long mapId) {
        return layerMapper.findByMapId(mapId);
    }

    /**
     * LAYER 파일 생성
     * @param layerDTO LayerDTO, uploadData MultipartFile
     */
    @Override
    public boolean createData(LayerDTO layerDTO, MultipartFile uploadData) {
        if(layerMapper.findByName(layerDTO.getName()) != null) return false;
        if(uploadData == null || uploadData.getSize() <= 0) return false; // 초기에는 TIFF / SHP 파일을 업로드 해야 한다.

        Date date = new Date();
        String layFilePath = String.format("%s%s/%s%s", dataPath, Constants.LAY_FILE_PATH, layerDTO.getName(), Constants.LAY_SUFFIX);

        String dataFilePath = "";

        // CADRG 인 경우에는 따로 설정한다.
        if(layerDTO.getType().equals("CADRG")){
            dataFilePath = String.format("%s%s/%s%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), CADRG_DEFAULT_EXECUTE_DIRECTORY + CADRG_DEFAULT_EXECUTE_FILE);
        } else {
            dataFilePath = String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
        }

        layerDTO.setLayerFilePath(layFilePath.replaceAll(dataPath, ""));
        layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));
        layerDTO.setDefault(false);

        layerDTO.setRegistTime(date);
        layerDTO.setUpdateTime(date);
        layerDTO.setVersion(DEFAULT_LAYER_VERSION);     // 기본 1.0
        setCommonProperties(layerDTO);

        if(layerMapper.insert(layerDTO) > 0){
            // 1단계. DB 생성 이후 파일을 로컬에 옮긴다.
            transferNewUploadFile(layerDTO, uploadData);

            // 2단계. *.lay 파일 생성
            try {
                String fileContext = MapServerUtil.fetchLayerFileContextWithDTO(defaultLayer, dataPath, layerDTO);
                FileSystemUtil.createAtFile(layFilePath, fileContext);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                return false;
            }

            log.info(layerDTO.getName() + " LAY 파일 생성 성공했습니다.");
            return true;
        }

        return false;
    }

    /**
     * LAYER 파일 수정
     * @param layerDTO LayerDTO, uploadData MultipartFile
     */
    @Override
    public boolean setData(LayerDTO layerDTO, MultipartFile uploadData) {
        LayerDTO selected = layerMapper.findByName(layerDTO.getName());
        if(selected == null) return false;

        layerDTO.setLayerFilePath(selected.getLayerFilePath());

        boolean isUploaded = (uploadData != null && uploadData.getSize() > 0);
        boolean isChanged = !isUploaded && !layerDTO.getMiddleFolder().equals(selected.getMiddleFolder());

        String dataFilePath;
        if(isUploaded){
            // CADRG 인 경우에는 따로 설정한다.
            if(layerDTO.getType().equals("CADRG")){
                dataFilePath = String.format("%s%s/%s%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), CADRG_DEFAULT_EXECUTE_DIRECTORY + CADRG_DEFAULT_EXECUTE_FILE);
            } else {
                dataFilePath = String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
            }
        } else if(isChanged) {
            dataFilePath = selected.getDataFilePath().replace(selected.getMiddleFolder(), layerDTO.getMiddleFolder());
        } else {
            dataFilePath = selected.getDataFilePath();
        }

        layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));
        layerDTO.setRegistTime(selected.getRegistTime()); // 추가 시간은 그대로 유지.
        layerDTO.setUpdateTime(new Date());     // update 시간 추가

        layerDTO.setVersion(Double.parseDouble(String.format("%.1f", (layerDTO.getVersion() + 0.1)))); // 버전은 뭐를 하든 0.1 로 높여준다.

        setCommonProperties(layerDTO);

        String layFilePath = String.format("%s%s", dataPath, selected.getLayerFilePath());

        // 새로운 파일이 업로드 되거나 중간 디렉토리가 교체 되었다면...
        if(isUploaded) {
            // 1단계. DB 갱신 이후 기존 레이어 리소스 파일 삭제
            removeLayerResourceFile(selected);
            // 2단계. 파일을 로컬에 옮긴다.
            transferNewUploadFile(layerDTO, uploadData);
        }

        // 경로가 바뀌면 위의 로직의 역순서로 진행.
        if(isChanged) {
            // 1단계. 파일을 로컬에 옮긴다.
            String source = dataPath + selected.getDataFilePath();
            String target = dataPath + layerDTO.getDataFilePath();

            File sourceFile = new File(source.replace(CADRG_DEFAULT_EXECUTE_FILE, ""));
            File targetFile = new File(target.replace(CADRG_DEFAULT_EXECUTE_FILE, ""));

            // 레이어 파일 타입이 CADRG ZIP 인 경우 디렉토리 단위를 옮겨야 한다.
            if(layerDTO.getType().equals("CADRG")) {
                try {
                    FileUtils.moveDirectory(sourceFile, targetFile);
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                    return false;
                }
            } else {
                // 1단계. 파일을 옮긴다.
                FileSystemUtil.moveFile(source, target);

                // 2단계. 기존 레이어 리소스 파일 삭제
                removeLayerResourceFile(selected);
            }
        }

        // 레이어 수정 전에 Map Version 관리도 추가 반영.
        mapVersionManagement.setLayerUpdateManage(layerDTO);

        if(layerMapper.update(layerDTO) > 0) {
            // *.lay 파일 생성
            try {
                String fileContext = MapServerUtil.fetchLayerFileContextWithDTO(defaultLayer, dataPath, layerDTO);
                FileSystemUtil.createAtFile(layFilePath, fileContext);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                return false;
            }

            log.info(layerDTO.getName() + " LAY 파일 수정 성공했습니다.");
            return true;
        }

        return false;
    }

    /**
     * LAYER 파일 삭제
     * @param id long
     */
    @Override
    public boolean removeData(long id) {
        LayerDTO selected = layerMapper.findById(id);
        if(selected == null) return false;

        mapLayerRelationMapper.deleteByLayerId(id);

        // 레이어 삭제 전에 Map Version 관리도 추가 반영.
        List<MapVersionDTO> mapVersions = mapVersionMapper.findByLayerId(id);
        if(mapVersions.size() > 0) mapVersionManagement.setLayerRemoveManage(selected);

        if(layerMapper.deleteById(id) > 0){
            // 1단계. 리소스 파일 삭제
            removeLayerResourceFile(selected);

            // 2단계. LAY 파일 삭제
            try {
                String mapFilePath = String.format("%s%s", dataPath, selected.getLayerFilePath());
                FileSystemUtil.deleteFile(mapFilePath);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }

            // 3단계. INCLUDE lay 파일 내용 삭제
            try {
                MapServerUtil.removeLayerIncludeSyntaxInMapFiles(dataPath + Constants.MAP_FILE_PATH, selected.getName());
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }

            log.info(selected.getName() + " LAY 파일 삭제 성공했습니다.");
            return true;
        }
        return false;
    }
}
