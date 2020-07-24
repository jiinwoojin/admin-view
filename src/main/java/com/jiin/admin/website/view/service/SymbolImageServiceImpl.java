package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.SymbolImageDTO;
import com.jiin.admin.mapper.data.SymbolImageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SymbolImageServiceImpl implements SymbolImageService {
    @Resource
    private SymbolImageMapper symbolImageMapper;

    @Override
    public List<SymbolImageDTO> findAll() {
        return symbolImageMapper.findAll();
    }
}
