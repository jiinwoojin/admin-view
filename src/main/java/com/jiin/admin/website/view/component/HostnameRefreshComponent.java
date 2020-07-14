package com.jiin.admin.website.view.component;

import com.jiin.admin.mapper.data.ContainerHistoryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class HostnameRefreshComponent {
    @Resource
    private ContainerHistoryMapper containerHistoryMapper;

    public boolean setHostnameInContainerHistoryList(String beforeName, String afterName){
        return containerHistoryMapper.updateHostnameData(beforeName, afterName) >= 0;
    }
}
