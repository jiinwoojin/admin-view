package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.ProxySelectModel;

public interface ProxyService {
    String loadDataDir();
    String loadProxyMainDir();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);
    Object loadDataModel(String type);
    ProxySelectModel loadProxySetting();
    boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel);
}
