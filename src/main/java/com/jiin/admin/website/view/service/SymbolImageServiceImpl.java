package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.mapper.data.SymbolImageMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.model.SymbolImageModel;
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
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

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

    /**
     * SYMBOL IMAGE 목록 출력
     */
    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(SymbolPageModel symbolPageModel) {
        return new HashMap<String, Object>() {{
            put("data", symbolImageMapper.findByPageModel(symbolPageModel));
            put("count", symbolImageMapper.countByPageModel(symbolPageModel));
        }};
    }

    /**
     * SYMBOL IMAGE 내용 및 FORM 출력
     */
    @Override
    public Map<String, Object> loadImageUpdateData(long id) {
        SymbolImageDTO dto = symbolImageMapper.findById(id);
        SymbolImageModel model = new SymbolImageModel();
        model.setName(dto == null ? "" : dto.getName());
        model.setDescription(dto == null ? "" : dto.getDescription());
        return new HashMap<String, Object>() {{
            put("model", model);
            put("data", dto);
        }};
    }

    @Override
    public byte[] loadImageByteArrayByName(String name) throws IOException {
        String path = dataPath + Constants.SYMBOL_FILE_PATH + String.format("/%s", name);
        String imageDir = path + String.format("/%s%s", name, Constants.PNG_SUFFIX);

        File image = new File(imageDir);
        if (!image.exists()) return null;

        InputStream is = new FileInputStream(image);
        BufferedImage bi = ImageIO.read(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);

        return baos.toByteArray();
    }

    @Override
    public byte[] loadPositionByteArrayByModel(String name, int x, int y, int width, int height) throws IOException {
        String path = dataPath + Constants.SYMBOL_FILE_PATH + String.format("/%s", name);
        String imageDir = path + String.format("/%s%s", name, Constants.PNG_SUFFIX);

        File image = new File(imageDir);
        if (!image.exists()) return null;

        InputStream is = new FileInputStream(image);
        BufferedImage bi = ImageIO.read(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi.getSubimage(x, y, width, height), "png", baos);

        return baos.toByteArray();
    }

    /**
     * SYMBOL 데이터 추가
     */
    @Override
    @Transactional
    public boolean createImageData(SymbolImageModel symbolImageModel) {
        if (symbolImageMapper.findByName(symbolImageModel.getName()) != null) {
            return false;
        }

        String imageName = symbolImageModel.getName();

        List<SymbolPositionModel> positions = ImageSpriteUtil.createImageSpriteArrayWithFiles(dataPath, imageName, symbolImageModel.getSprites());

        if (positions.size() < 1) {
            return false;
        }

        long idx = symbolImageMapper.findNextSeqVal();
        AccountEntity user = (AccountEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SymbolImageDTO symbolImageDTO = new SymbolImageDTO(idx, symbolImageModel.getName(), symbolImageModel.getDescription(), new Date(), user.getUsername(), user.getName(), false);

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

    @Override
    public boolean setImageData(SymbolImageModel symbolImageModel) {
        // TODO IMAGE UPDATE 및 새로운 사진 업로드 반영할 것.
        return false;
    }

    /**
     * SYMBOL 이미지 삭제
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

    /**
     * SYMBOL 데이터 삭제
     */
    @Override
    public boolean deletePositionData(long imageId, List<Long> ids) {
        SymbolImageDTO imageDTO = symbolImageMapper.findById(imageId);
        if (imageDTO == null) {
            return false;
        }
        String path = dataPath + Constants.SYMBOL_FILE_PATH + String.format("/%s", imageDTO.getName());
        String imageDir = path + String.format("/%s%s", imageDTO.getName(), Constants.PNG_SUFFIX);

        File image = new File(imageDir);
        if (!image.exists()) return false;

        InputStream is = null;
        try {
            is = new FileInputStream(image);
            BufferedImage bi = ImageIO.read(is);

            Map<String, BufferedImage> remainMap = new LinkedHashMap<>();
            for (SymbolPositionDTO position : imageDTO.getPositions()) {
                if (!ids.contains(position.getId())) {
                    remainMap.put(position.getName(), bi.getSubimage(position.getXPos(), position.getYPos(), position.getWidth(), position.getHeight()));
                }
            }

            symbolPositionMapper.deleteByIdIn(ids);

            List<SymbolPositionModel> models = ImageSpriteUtil.updateSpriteArrayWithImageArray(remainMap, dataPath, imageDTO.getName());

            int cnt = 0;
            for (SymbolPositionModel model : models) {
                model.setImageId(imageId);
                cnt += symbolPositionMapper.updateByModelAndImageId(model);
            }

            return models.size() == cnt;
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return false;
    }
}
