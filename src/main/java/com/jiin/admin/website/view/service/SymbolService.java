package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.website.security.AccountAuthProvider;
import com.jiin.admin.website.security.AccountService;
import com.jiin.admin.website.view.mapper.SymbolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SymbolService {

    @Resource
    private SymbolMapper mapper;

    public List<MapSymbol> list() {
        return mapper.list();
    }
}
