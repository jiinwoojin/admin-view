package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SymbolImageService {
    List<OptionModel> loadSearchByOptionList();
    List<OptionModel> loadOrderByOptionList();
    List<OptionModel> loadPositionOrderByOptionList();

    String loadJSONContextByImageId(long imageId);

    Map<String, Object> loadDataListAndCountByPaginationModel(SymbolImagePageModel symbolImagePageModel);
    Map<String, Object> loadImageUpdateDataByPageModel(SymbolPositionPageModel symbolPositionPageModel);
    byte[] loadImageByteArrayByName(String name) throws IOException;
    byte[] loadPositionByteArrayByModel(String name, int x, int y, int width, int height) throws IOException;
    byte[] loadImageByteByPositionId(long positionId) throws IOException;

    boolean createImageData(SymbolImageModel symbolImageModel);
    boolean setImageData(SymbolImageModel symbolImageModel);
    boolean setPositionData(SymbolPositionEditModel symbolPositionEditModel);

    boolean deleteImageData(long id);
    boolean deletePositionData(long imageId, List<Long> ids);
}
