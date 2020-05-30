package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapLayerRelationMapper;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapServerUtil;
import lombok.extern.slf4j.Slf4j;
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
                    log.error(layerDTO.getName() + " DATA 디렉토리 권한 설정 실패했습니다.");
                    e.printStackTrace();
                }
            }
        }

        // 2단계. 레이어 리소스 파일 업로드 뒤 옮기기
        File dataFile = new File(String.format("%s/%s", dataDirStr, uploadFile.getOriginalFilename()));
        try {
            uploadFile.transferTo(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(layerDTO.getName() + " DATA 파일 옮기기 실패했습니다.");
        }
    }

    /**
     * LAYER 리소스 파일을 삭제한다.
     * @param layerDTO LayerDTO
     */
    private void removeLayerResourceFile(LayerDTO layerDTO){
        String dataFilePath = dataPath + layerDTO.getDataFilePath();
        try {
            FileSystemUtil.deleteFile(dataFilePath);
        } catch (IOException e) {
            log.error("레이어 " + layerDTO.getName() + " DATA 파일 삭제 실패했습니다.");
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
        layerDTO.setRegistTime(new Date());
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

        String layFilePath = String.format("%s%s/%s%s", dataPath, Constants.LAY_FILE_PATH, layerDTO.getName(), Constants.LAY_SUFFIX);
        String dataFilePath = String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
        layerDTO.setLayerFilePath(layFilePath.replaceAll(dataPath, ""));
        layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));
        layerDTO.setDefault(false);
        setCommonProperties(layerDTO);

        if(layerMapper.insert(layerDTO) > 0){
            // 1단계. DB 생성 이후 파일을 로컬에 옮긴다.
            transferNewUploadFile(layerDTO, uploadData);

            // 2단계. *.lay 파일 생성
            try {
                String fileContext = MapServerUtil.fetchLayerFileContextWithDTO(defaultLayer, dataPath, layerDTO);
                FileSystemUtil.createAtFile(layFilePath, fileContext);
            } catch (IOException e) {
                log.error(layerDTO.getName() + " LAY 파일 생성 실패했습니다.");
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

        boolean isUploaded = (uploadData != null && uploadData.getSize() > 0);

        if(isUploaded){
            String dataFilePath = String.format("%s%s/%s/%s", dataPath, Constants.DATA_PATH, layerDTO.getMiddleFolder(), uploadData.getOriginalFilename());
            layerDTO.setDataFilePath(dataFilePath.replaceAll(dataPath, ""));
        }
        layerDTO.setLayerFilePath(selected.getLayerFilePath());
        setCommonProperties(layerDTO);

        if(layerMapper.update(layerDTO) > 0){
            String layFilePath = String.format("%s%s", dataPath, selected.getLayerFilePath());

            // 새로운 파일이 업로드 되었다면...
            if(isUploaded) {
                // 1단계. DB 갱신 이후 기존 레이어 리소스 파일 삭제
                removeLayerResourceFile(selected);

                // 2단계. 파일을 로컬에 옮긴다.
                transferNewUploadFile(layerDTO, uploadData);
            }

            // *.lay 파일 생성
            try {
                String fileContext = MapServerUtil.fetchLayerFileContextWithDTO(defaultLayer, dataPath, layerDTO);
                FileSystemUtil.createAtFile(layFilePath, fileContext);
            } catch (IOException e) {
                log.error(layerDTO.getName() + " LAY 파일 수정 실패했습니다.");
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
        if(layerMapper.deleteById(id) > 0){
            // 1단계. 리소스 파일 삭제
            removeLayerResourceFile(selected);

            // 2단계. LAY 파일 삭제
            try {
                String mapFilePath = String.format("%s%s", dataPath, selected.getLayerFilePath());
                FileSystemUtil.deleteFile(mapFilePath);
            } catch (IOException e) {
                log.error(selected.getName() + " LAY 파일 삭제 실패했습니다.");
            }

            // 3단계. INCLUDE lay 파일 내용 삭제
            try {
                MapServerUtil.removeLayerIncludeSyntaxInMapFiles(dataPath + Constants.MAP_FILE_PATH, selected.getName());
            } catch (IOException e) {
                log.error(selected.getName() + " MAP INCLUDE SYNTEX 내용 삭제 실패했습니다.");
            }

            log.info(selected.getName() + " LAY 파일 삭제 성공했습니다.");
            return true;
        }
        return false;
    }
}
