package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.mapper.data.SymbolImageMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.ImageSpriteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * 이미 저장된 SPRITE 이미지 목록들을 불러온다.
     */
    private Map<String, BufferedImage> loadStoreImageListByImageId(long imageId) {
        SymbolImageDTO imageDTO = symbolImageMapper.findById(imageId);
        if (imageDTO == null) {
            return new HashMap<>();
        }


        String path = dataPath + imageDTO.getImageFilePath();

        File image = new File(path);
        if (!image.exists()) {
            return new HashMap<>();
        }

        InputStream is = null;
        try {
            is = new FileInputStream(image);
            BufferedImage bi = ImageIO.read(is);

            Map<String, BufferedImage> remainMap = new LinkedHashMap<>();
            for (SymbolPositionDTO position : imageDTO.getPositions()) {
                remainMap.put(position.getName(), bi.getSubimage(position.getXPos(), position.getYPos(), position.getWidth(), position.getHeight()));
            }

            return remainMap;
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return new HashMap<>();
    }

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
    public String loadJSONContextByImageId(long imageId) {
        SymbolImageDTO dto = symbolImageMapper.findById(imageId);
        if (dto == null) {
            return "";
        }
        try {
            return FileSystemUtil.fetchFileContext(dataPath + dto.getJsonFilePath());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return "ERROR";
        }
    }

    /**
     * SYMBOL 그룹 목록 출력
     */
    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(SymbolPageModel symbolPageModel) {
        return new HashMap<String, Object>() {{
            put("data", symbolImageMapper.findByPageModel(symbolPageModel));
            put("count", symbolImageMapper.countByPageModel(symbolPageModel));
        }};
    }

    /**
     * SYMBOL 그룹 내용 및 FORM 출력
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

    /**
     * SYMBOL 그룹 단위 이미지 byte 코드
     */
    @Override
    public byte[] loadImageByteArrayByName(String name) throws IOException {
        return loadPositionByteArrayByModel(name, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * SYMBOL SPRITE 단위 이미지 byte 코드
     */
    @Override
    public byte[] loadPositionByteArrayByModel(String name, int x, int y, int width, int height) throws IOException {
        SymbolImageDTO imageDTO = symbolImageMapper.findByName(name);
        if (imageDTO == null) {
            return null;
        }

        String path = dataPath + imageDTO.getImageFilePath();

        File image = new File(path);
        if (!image.exists()) return null;

        InputStream is = new FileInputStream(image);
        BufferedImage bi = ImageIO.read(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (x == Integer.MIN_VALUE && y == Integer.MIN_VALUE && width == Integer.MAX_VALUE && height == Integer.MAX_VALUE) {
            ImageIO.write(bi, "png", baos);
        } else {
            ImageIO.write(bi.getSubimage(x, y, width, height), "png", baos);
        }

        return baos.toByteArray();
    }

    /**
     * SYMBOL 그룹 PNG, JSON 추가
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
                SymbolPositionDTO pos = new SymbolPositionDTO(0L, position.getName(), position.getHeight(), position.getWidth(), position.getPixelRatio(), position.getX(), position.getY(), idx, position.getPngBytes());
                cnt += symbolPositionMapper.insert(pos);
            }

            return cnt == positions.size();
        } else {
            return false;
        }
    }

    /**
     * SYMBOL 그룹 수정 (PNG SPRITE 데이터 추가)
     */
    @Override
    @Transactional
    public boolean setImageData(SymbolImageModel symbolImageModel) {
        SymbolImageDTO imageDTO = symbolImageMapper.findByName(symbolImageModel.getName());
        if (imageDTO == null) {
            return false;
        }

        AccountEntity user = (AccountEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        imageDTO.setUpdateTime(new Date());
        imageDTO.setRegistorId(user.getUsername());
        imageDTO.setRegistorId(user.getName());
        imageDTO.setDescription(symbolImageModel.getDescription());

        Map<String, BufferedImage> images = this.loadStoreImageListByImageId(imageDTO.getId());
        for (MultipartFile sprite : symbolImageModel.getSprites()) {
            try {
                BufferedImage bi = ImageIO.read(sprite.getInputStream());
                String suffix = sprite.getOriginalFilename().replace(".png", "").replace(".PNG", "");
                images.put(suffix, bi);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        List<SymbolPositionModel> models = ImageSpriteUtil.updateSpriteArrayWithImageArray(images, dataPath, imageDTO.getName());
        int count = 0;
        for (SymbolPositionModel model : models) {
            model.setImageId(imageDTO.getId());
            if (symbolPositionMapper.findByNameAndImageId(model.getName(), imageDTO.getId()) != null) {
                count += symbolPositionMapper.updateByModelAndImageId(model);
            } else {
                count += symbolPositionMapper.insert(SymbolPositionModel.convertDTO(model));
            }
        }

        return symbolImageMapper.update(imageDTO) > 0 && count == models.size();
    }

    /**
     * SYMBOL SPRITE 단위 부분 수정
     */
    @Override
    @Transactional
    public boolean setPositionData(SymbolPositionEditModel symbolPositionEditModel) {
        SymbolImageDTO imageDTO = symbolImageMapper.findById(symbolPositionEditModel.getImageId());
        if (imageDTO == null) {
            return false;
        }

        MultipartFile file = symbolPositionEditModel.getSpriteImage();
        Map<String, BufferedImage> images = this.loadStoreImageListByImageId(symbolPositionEditModel.getImageId());
        if (file != null && !file.isEmpty()) {
            try {
                BufferedImage bi = ImageIO.read(file.getInputStream());
                images.remove(symbolPositionEditModel.getBeforeName());
                images.put(symbolPositionEditModel.getSpriteName(), bi);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        } else {
            BufferedImage bi = images.get(symbolPositionEditModel.getBeforeName());
            images.remove(symbolPositionEditModel.getBeforeName());
            images.put(symbolPositionEditModel.getSpriteName(), bi);
        }

        SymbolPositionDTO position = symbolPositionMapper.findByNameAndImageId(symbolPositionEditModel.getBeforeName(), symbolPositionEditModel.getImageId());
        if (position != null) {
            symbolPositionMapper.deleteById(position.getId());
        }

        List<SymbolPositionModel> models = ImageSpriteUtil.updateSpriteArrayWithImageArray(images, dataPath, imageDTO.getName());
        int count = 0;
        for (SymbolPositionModel model : models) {
            model.setImageId(imageDTO.getId());
            if (symbolPositionMapper.findByNameAndImageId(model.getName(), imageDTO.getId()) != null) {
                count += symbolPositionMapper.updateByModelAndImageId(model);
            } else {
                count += symbolPositionMapper.insert(SymbolPositionModel.convertDTO(model));
            }
        }

        return count == models.size();
    }

    /**
     * SYMBOL 그룹 PNG, JSON 삭제
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
     * SYMBOL SPRITE 단위 삭제
     */
    @Override
    public boolean deletePositionData(long imageId, List<Long> ids) {
        SymbolImageDTO imageDTO = symbolImageMapper.findById(imageId);
        if (imageDTO == null) {
            return false;
        }

        Map<String, BufferedImage> images = this.loadStoreImageListByImageId(imageId);
        for(SymbolPositionDTO position : imageDTO.getPositions()) {
            if (ids.contains(position.getId())) {
                images.remove(position.getName());
            }
            symbolPositionMapper.deleteByIdIn(ids);
        }

        List<SymbolPositionModel> models = ImageSpriteUtil.updateSpriteArrayWithImageArray(images, dataPath, imageDTO.getName());

        int cnt = 0;
        for (SymbolPositionModel model : models) {
            model.setImageId(imageId);
            cnt += symbolPositionMapper.updateByModelAndImageId(model);
        }

        return models.size() == cnt;
    }
}
