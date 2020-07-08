package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.entity.SymbolPositionEntity;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.view.mapper.SymbolMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SymbolService {
    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private SymbolMapper mapper;

    public List<MapSymbol> list() {
        return mapper.list();
    }

    /**
     * SYMBOL 정렬 조건 옵션 목록
     */
    public List<OptionModel> symbolOrderByOptions() {
        return Arrays.asList(
            new OptionModel("-- 정렬 방식 선택 --", 0),
            new OptionModel("이름 순서 정렬", 1),
            new OptionModel("이름 순서 역정렬", 2),
            new OptionModel("X 좌표 정렬", 3),
            new OptionModel("X 좌표 역정렬", 4),
            new OptionModel("Y 좌표 정렬", 5),
            new OptionModel("Y 좌표 역정렬", 6)
        );
    }

    public Map<String, Object> findSymbolPositionsByPagination(int pg, int sz, int ob, String st) {
        final Sort[] sorts = {
            Sort.by("id"),
            Sort.by("name"),
            Sort.by("name").descending(),
            Sort.by("x_pos"),
            Sort.by("x_pos").descending(),
            Sort.by("y_pos"),
            Sort.by("y_pos").descending()
        };

        List<SymbolPositionEntity> sbRes = mapper.findAllPositionsByPagination(st, ob);
        Pageable pageable = PageRequest.of(pg - 1, sz, sorts[ob]);

        int pageSize = pageable.getPageSize();
        long pageOffset = pageable.getOffset();
        long pageEnd = (pageOffset + pageSize) > sbRes.size() ? sbRes.size() : pageOffset + pageSize;

        Page<SymbolPositionEntity> page = new PageImpl<>(sbRes.subList((int) pageOffset, (int) pageEnd), pageable, sbRes.size());

        Map<String, Object> map = new HashMap<>();
        map.put("count", page.getTotalElements());
        map.put("data", page.getContent());

        return map;
    }

    public String getSymbolJSONContext() throws IOException {
        String path = dataPath + "/html/GSymbol";
        String jsonDir = path + "/GSSSymbol.json";

        StringBuffer sb = new StringBuffer();
        FileReader reader = new FileReader(jsonDir);
        BufferedReader bufReader = new BufferedReader(reader);
        String line = "";
        while((line = bufReader.readLine()) != null) {
            sb.append(line + "\n");
        }

        return sb.length() == 0 ? "[ NO CONTEXT ]" : sb.toString();
    }

    public byte[] getSymbolSetImages() throws IOException {
        String path = dataPath + "/html/GSymbol";
        String imageDir = path + "/GSSSymbol.png";

        File image = new File(imageDir);
        if (!image.exists()) return null;

        InputStream is = new FileInputStream(image);
        BufferedImage bi = ImageIO.read(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);

        return baos.toByteArray();
    }

    public byte[] getSymbolPartsWithPos(int xPos, int yPos, int width, int height) throws IOException {
        String path = dataPath + "/html/GSymbol";
        String imageDir = path + "/GSSSymbol.png";

        File image = new File(imageDir);
        if (!image.exists()) return null;

        InputStream is = new FileInputStream(image);
        BufferedImage bi = ImageIO.read(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi.getSubimage(xPos, yPos, width, height), "png", baos);
        return baos.toByteArray();
    }
}
