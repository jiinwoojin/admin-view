package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.model.SymbolImageModel;
import com.jiin.admin.website.model.SymbolPageModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SymbolImageService {
    List<OptionModel> loadSearchByOptionList();
    List<OptionModel> loadOrderByOptionList();

    Map<String, Object> loadDataListAndCountByPaginationModel(SymbolPageModel symbolPageModel);
    Map<String, Object> loadImageUpdateData(long id);
    byte[] loadImageByteArrayByName(String name) throws IOException;
    byte[] loadPositionByteArrayByModel(String name, int x, int y, int width, int height) throws IOException;

    boolean createImageData(SymbolImageModel symbolImageModel);
    boolean setImageData(SymbolImageModel symbolImageModel);

    boolean deleteImageData(long id);
    boolean deletePositionData(long imageId, List<Long> ids);
}
