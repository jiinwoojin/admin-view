package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.mapper.data.SymbolImageMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.model.SymbolImageCreateModel;
import com.jiin.admin.website.model.SymbolPageModel;
import com.jiin.admin.website.model.SymbolPositionModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.ImageSpriteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SymbolImageServiceImpl implements SymbolImageService {
    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private SymbolImageMapper symbolImageMapper;

    @Resource
    private SymbolPositionMapper symbolPositionMapper;

    private static final List<OptionModel> sbOptions = Arrays.asList(
            new OptionModel("-- 검색 키워드 선택 --", 0),
            new OptionModel("SYMBOL 이미지 이름", 1),
            new OptionModel("SYMBOL 이미지 등록자", 2)
    );

    private static final List<OptionModel> obOptions = Arrays.asList(
            new OptionModel("-- 정렬 방식 선택 --", 0),
            new OptionModel("ID 순서 정렬", 1),
            new OptionModel("이름 순서 정렬", 2),
            new OptionModel("등록 기간 역순 정렬", 3)
    );

    /**
     * SYMBOL 검색 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadSearchByOptionList() {
        return sbOptions;
    }

    /**
     * SYMBOL 정렬 조건 옵션 목록
     */
    @Override
    public List<OptionModel> loadOrderByOptionList() {
        return obOptions;
    }

    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(SymbolPageModel symbolPageModel) {
        return new HashMap<String, Object>() {{
            put("data", symbolImageMapper.findByPageModel(symbolPageModel));
            put("count", symbolImageMapper.countByPageModel(symbolPageModel));
        }};
    }

    /**
     * SYMBOL 데이터 추가
     */
    @Override
    @Transactional
    public boolean createImageData(SymbolImageCreateModel symbolImageCreateModel) {
        if (symbolImageMapper.findByName(symbolImageCreateModel.getName()) != null) {
            return false;
        }

        String imageName = symbolImageCreateModel.getName();

        List<SymbolPositionModel> positions = ImageSpriteUtil.createImageSpriteArrayWithFiles(dataPath, imageName, symbolImageCreateModel.getSprites());

        if (positions.size() < 1) {
            return false;
        }

        long idx = symbolImageMapper.findNextSeqVal();
        AccountEntity user = (AccountEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SymbolImageDTO symbolImageDTO = new SymbolImageDTO(idx, symbolImageCreateModel.getName(), symbolImageCreateModel.getDescription(), new Date(), user.getUsername(), user.getName(), false);

        String png1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_SUFFIX);
        String png2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_2X_SUFFIX);

        String json1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_SUFFIX);
        String json2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_2X_SUFFIX);

        symbolImageDTO.setImageFilePath(png1XPath.replace(dataPath, ""));
        symbolImageDTO.setImage2xFilePath(png2XPath.replace(dataPath, ""));
        symbolImageDTO.setJsonFilePath(json1XPath.replace(dataPath, ""));
        symbolImageDTO.setJson2xFilePath(json2XPath.replace(dataPath, ""));

        if (symbolImageMapper.insert(symbolImageDTO) > 0) {
            int cnt = 0;
            for (SymbolPositionModel position : positions) {
                SymbolPositionDTO pos = new SymbolPositionDTO(0L, position.getName(), position.getHeight(), position.getWidth(), position.getPixelRatio(), position.getX(), position.getY(), idx);
                cnt += symbolPositionMapper.insert(pos);
            }
            return cnt == positions.size();
        } else {
            return false;
        }
    }

    /**
     * SYMBOL 데이터 삭제
     */
    @Override
    @Transactional
    public boolean deleteImageData(long id) {
        SymbolImageDTO image = symbolImageMapper.findById(id);
        if (image == null) {
            return false;
        }

        String folder = dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, image.getName());
        if (!StringUtils.isBlank(image.getName())) {
            try {
                FileSystemUtil.deleteFile(folder);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return symbolPositionMapper.deleteByImageId(image.getId()) > 0 && symbolImageMapper.deleteById(image.getId()) > 0;
    }
}
