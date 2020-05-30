package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.OptionModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LayerService {
    List<OptionModel> loadSearchByOptionList();
    List<OptionModel> loadOrderByOptionList();

    Map<String, Object> loadDataListAndCountByPaginationModel(LayerPageModel layerPageModel);
    List<LayerDTO> loadDataList();
    List<LayerDTO> loadDataListByMapId(long mapId);

    boolean createData(LayerDTO layerDTO, MultipartFile uploadData);
    boolean setData(LayerDTO layerDTO, MultipartFile uploadData);
    boolean removeData(long id);
}
