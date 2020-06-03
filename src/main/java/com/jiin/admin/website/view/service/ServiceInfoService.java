package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.GeoContainerInfo;

import java.util.Map;

public interface ServiceInfoService {
    Map<String, GeoContainerInfo> loadGeoContainerMap();
}
