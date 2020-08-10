package com.jiin.admin.website.view.service;

import com.amihaiemil.docker.Container;
import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.vo.SeedContainerInfo;

import java.util.List;
import java.util.Map;

public interface ProxySeedService {
    List<ProxyCacheDTO> loadProxyCacheListBySelected();
    List<SeedContainerInfo> loadSeedContainerInfoList();
    boolean removeSeedContainerByName(String name);
    SeedContainerInfo createSeedContainer(Map<String, Object> param);
    String resetDefaultSeeding();
    Map<String, Object> loadLogTextInContainerByName(String name);
}
