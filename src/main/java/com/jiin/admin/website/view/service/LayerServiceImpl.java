package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.OptionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LayerServiceImpl implements LayerService {
    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map.map")
    private File defaultMap;

    @Resource
    private LayerMapper layerMapper;

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

    @Override
    public Map<String, Object> loadDataListAndCountByPaginationModel(LayerPageModel layerPageModel) {
        List<LayerDTO> layerList = layerMapper.findByPageModel(layerPageModel);
        return new HashMap<String, Object>(){{
            put("data", layerList);
            put("count", layerList.size());
        }};
    }

    @Override
    public List<LayerDTO> loadDataList() {
        return layerMapper.findAll();
    }

    @Override
    public List<LayerDTO> loadDataListByMapId(long mapId) {
        return layerMapper.findByMapId(mapId);
    }
}
