package com.jiin.admin.website.view.service;

import com.jiin.admin.website.view.mapper.ManageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ManageService {

    @Resource
    private ManageMapper mapper;

    public List<Map> getSourceList() {
        return mapper.getSourceList();
    }

    public List<Map> getLayerList() {
        return mapper.getLayerList();
    }
}
