package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.model.SymbolPageModel;

import java.util.List;
import java.util.Map;

public interface SymbolImageService {
    List<OptionModel> loadSearchByOptionList();
    List<OptionModel> loadOrderByOptionList();

    Map<String, Object> loadDataListAndCountByPaginationModel(SymbolPageModel symbolPageModel);
}
