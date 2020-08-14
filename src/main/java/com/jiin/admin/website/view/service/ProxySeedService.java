package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxySeedCronDTO;
import com.jiin.admin.vo.SeedContainerInfo;

import java.util.List;
import java.util.Map;

public interface ProxySeedService {
    List<ProxyCacheDTO> loadProxyCacheListBySelected();
    List<SeedContainerInfo> loadSeedContainerInfoList();
    ProxySeedCronDTO loadSeedCronScheduleByCache(String name);
    boolean removeSeedContainerByName(String name);
    boolean removeSeedYAMLFileNotNeed();
    SeedContainerInfo createSeedContainer(Map<String, Object> param);
    String resetDefaultSeeding();
    Map<String, Object> loadLogTextInContainerByName(String name);
    Map<String, Integer> setCacheSeedingCleanUpSetting(Map<String, Object> param);
    boolean setSeedCronSchedule(Map<String, Object> param);
    boolean removeSeedCycleByCacheName(String name);
}
