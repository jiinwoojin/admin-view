package com.jiin.admin.website.view.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.model.OptionModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MapService {
    List<OptionModel> loadSearchByOptionList();
    List<OptionModel> loadOrderByOptionList();
    Map<String, Object> loadDataListAndCountByPaginationModel(MapPageModel mapPageModel);
    MapDTO loadDataById(long id);
    boolean createData(MapDTO mapDTO, String layers) throws IOException;
    boolean setData(MapDTO mapDTO, String layers) throws JsonProcessingException;
    boolean removeData(long id) throws IOException;
}