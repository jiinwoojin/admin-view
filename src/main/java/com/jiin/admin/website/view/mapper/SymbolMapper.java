package com.jiin.admin.website.view.mapper;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.website.security.AccountAuthProvider;
import com.jiin.admin.website.security.AccountService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@BaseMapper
public interface SymbolMapper {

    @Select("SELECT * FROM MAP_SYMBOL")
    List<MapSymbol> list();
}
