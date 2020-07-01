package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.config.SessionService;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapVersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapLayerRelationMapper;
import com.jiin.admin.mapper.data.MapVersionMapper;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapServerUtil;
import com.jiin.admin.website.util.VectorShapeUtil;
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

    @Resource
    private SessionService session;

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
        // 1단계. 레이어 리소스 파일 디렉토리 생성
        String dataDirStr = dataPath + Constants.DATA_PATH + "/" + layerDTO.getMiddleFolder();
        File dataDir = new File(dataDirStr);
        if(!dataDir.exists()) dataDir.mkdirs();

        // 2단계. 레이어 리소스 파일 업로드 뒤 옮기기
        String filename = uploadFile.getOriginalFilename();
        File dataFile = new File(String.format("%s/%s", dataDirStr, filename));
        try {
            uploadFile.transferTo(dataFile);

            // SHP 파일인 경우에는 파일 압축 해제를 진행해야 한다.
            if(layerDTO.getType().equals("VECTOR")){
                FileSystemUtil.decompressZipFile(dataFile);
                FileSystemUtil.deleteFile(dataFile.getPath()); // 파일 업로드가 완료되면 삭제한다.
            }
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        // 3단계. DATA 폴더 하단부로 755 권한 설정.
        if(!FileSystemUtil.isWindowOS()) {
            try {
                String middleFirst = layerDTO.getMiddleFolder().split("/")[0];
                FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(new File(dataPath + Constants.DATA_PATH + String.format("/%s", middleFirst)));
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * LAYER 리소스 파일을 삭제한다.
     * @param layerDTO LayerDTO
     */
    private void removeLayerResourceFile(LayerDTO layerDTO){
        String dataFilePath = dataPath + layerDTO.getDataFilePath();
        try {
            switch(layerDTO.getType()){
                case "RASTER" :
                case "CADRG" :
                    FileSystemUtil.deleteFile(dataFilePath);
                    break;
                case "VECTOR" :
                    String filename = new File(dataFilePath).getName();
                    FileSystemUtil.deleteFile(dataFilePath.replace(String.format("/%s", filename), ""));
                    break;
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
     * 실제 경로에 저장할 dataFilePath 를 구해주는 메소드
     * @param layerDTO LayerDTO
     */
    private String loadCommonDataFilePath(LayerDTO layerDTO, MultipartFile uploadData){
        switch(layerDTO.getType()){
            case "CADRG" :
                // CADRG 인 경우 : /${data-dir}/${MIDDLE-FOLDER}/abc.zip
                return String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
            case "VECTOR" :
                // VECTOR 인 경우 : /${data-dir}/${MIDDLE-FOLDER}/abc.shp
                String shpFile = VectorShapeUtil.loadRealShpFileInZipFile(uploadData);
                if(shpFile.equals("NONE")) {
                    log.error("VECTOR ZIP 파일에는 *.SHP 파일이 꼭 포함되어 있어야 합니다.");
                    return "NONE";
                }
                if(!VectorShapeUtil.confirmIsZipFileContainsSHPFile(uploadData)) {
                    log.error("VECTOR ZIP 파일에는 *.SHP, *.SHX, *.DBF 파일이 동봉되어 있어야 합니다.");
                    return "NONE";
                }
                return String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), shpFile);
            case "RASTER" : // RASTER 인 경우 : /${data-dir}/${MIDDLE-FOLDER}/abc.tif
                return String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
            default :
                return "NONE";
        }
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
        if(uploadData == null || uploadData.getSize() <= 0) return false;

        Date date = new Date();
        String layFilePath = String.format("%s%s/%s%s", dataPath, Constants.LAY_FILE_PATH, layerDTO.getName(), Constants.LAY_SUFFIX);

        String dataFilePath = loadCommonDataFilePath(layerDTO, uploadData);

        if(dataFilePath.equals("NONE")) return false;

        layerDTO.setLayerFilePath(layFilePath.replaceAll(dataPath, ""));
        layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));
        layerDTO.setDefault(false);

        layerDTO.setRegistTime(date);
        layerDTO.setUpdateTime(date);
        layerDTO.setVersion(Double.parseDouble(String.format("%.1f", Constants.DEFAULT_LAYER_VERSION)));     // 기본 1.0
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

        boolean isUploaded = (uploadData != null);
        boolean isChanged = !isUploaded && !layerDTO.getMiddleFolder().equals(selected.getMiddleFolder());

        String dataFilePath;
        if(isUploaded) {
            dataFilePath = loadCommonDataFilePath(layerDTO, uploadData);
        } else {
            dataFilePath = Constants.DATA_PATH + "/" + layerDTO.getMiddleFolder() + selected.getDataFilePath().replace(Constants.DATA_PATH + "/" + selected.getMiddleFolder(), "");
        }

        layerDTO.setRegistTime(selected.getRegistTime()); // 추가 시간은 그대로 유지.
        layerDTO.setUpdateTime(new Date());     // update 시간 추가
        layerDTO.setVersion(Double.parseDouble(String.format("%.1f", (layerDTO.getVersion() + Constants.ASCEND_LAYER_VERSION)))); // 버전은 뭐를 하든 0.1 로 높여준다.
        layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));

        setCommonProperties(layerDTO);

        String layFilePath = String.format("%s%s", dataPath, selected.getLayerFilePath());

        if(dataFilePath.equals("NONE")) return false;

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
            String source, target;

            if(layerDTO.getType().equals("VECTOR")){
                source = dataPath + Constants.DATA_PATH + "/" + selected.getMiddleFolder();
                target = dataPath + Constants.DATA_PATH + "/" + layerDTO.getMiddleFolder();
            } else {
                source = dataPath + selected.getDataFilePath();
                target = dataPath + Constants.DATA_PATH + "/" + layerDTO.getMiddleFolder() + selected.getDataFilePath().replace(Constants.DATA_PATH + "/" + selected.getMiddleFolder(), "");
            }

            File sourceFile = new File(source);
            File targetFile = new File(target);

            // 레이어 파일 타입이 VECTOR 인 경우 디렉토리 단위를 옮겨야 한다. (SHP, SHX, DBF 등)
            if(layerDTO.getType().equals("VECTOR")) {
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

            if(!FileSystemUtil.isWindowOS()) {
                // 디렉토리 변경이 끝나면 모든 권한을 755 로 설정.
                try {
                    String middleFirst = layerDTO.getMiddleFolder().split("/")[0];
                    FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(new File(dataPath + Constants.DATA_PATH + String.format("/%s", middleFirst)));
                } catch (IOException e) {
                    log.error("ERROR - " + e.getMessage());
                }
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
