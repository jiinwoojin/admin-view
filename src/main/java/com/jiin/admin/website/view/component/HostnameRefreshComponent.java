package com.jiin.admin.website.view.component;

import com.jiin.admin.mapper.data.ContainerHistoryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 서버 HOSTNAME 이 변경될 때, 실행 정보에 대한 HISTORY 의 HOSTNAME 을 정정하기 위한 컴포넌트.
@Component
public class HostnameRefreshComponent {
    @Resource
    private ContainerHistoryMapper containerHistoryMapper;

    public boolean setHostnameInContainerHistoryList(String beforeName, String afterName){
        return containerHistoryMapper.updateHostnameData(beforeName, afterName) >= 0;
    }
}
