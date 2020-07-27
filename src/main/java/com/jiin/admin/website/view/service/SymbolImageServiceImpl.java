package com.jiin.admin.website.view.service;

import com.jiin.admin.mapper.data.SymbolImageMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.model.SymbolPageModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SymbolImageServiceImpl implements SymbolImageService {
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
}
