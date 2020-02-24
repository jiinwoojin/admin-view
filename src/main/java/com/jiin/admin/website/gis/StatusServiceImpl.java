package com.jiin.admin.website.gis;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatusServiceImpl implements StatusService {
    private static final String CENTER_A = "CENTER_A";
    private static final String CENTER_B = "CENTER_B";
    private static final String CENTER_C = "CENTER_C";

    private static final String ON = "ON";
    private static final String OFF = "OFF";
    private static final String ERROR = "ERROR";

    @Override
    public Map<String, String> centerStatusCheck() {
        Map<String, String> statusMap = new TreeMap<>();
        List<String> statusElement = Arrays.asList(ON, OFF, ERROR);
        Random random = new Random();
        statusMap.put(CENTER_A, statusElement.get(random.nextInt(statusElement.size())));
        statusMap.put(CENTER_B, statusElement.get(random.nextInt(statusElement.size())));
        statusMap.put(CENTER_C, statusElement.get(random.nextInt(statusElement.size())));
        return statusMap;
    }

    @Override
    public Map<String, String> centerSynchronizeCheck() {
        // 임시 로직만 작성. 내용 변동 가능성 有.
        Map<String, String> statusMap = new TreeMap<>();
        List<String> statusElement = Arrays.asList(ON, OFF, ERROR);
        Random random = new Random();
        statusMap.put(CENTER_A, statusElement.get(random.nextInt(statusElement.size())));
        statusMap.put(CENTER_B, statusElement.get(random.nextInt(statusElement.size())));
        statusMap.put(CENTER_C, statusElement.get(random.nextInt(statusElement.size())));
        return statusMap;
    }
}
