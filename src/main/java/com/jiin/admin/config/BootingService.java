package com.jiin.admin.config;


import com.jiin.admin.entity.MapSymbol;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Service
public class BootingService {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void initializeSymbol() {
        entityManager.createQuery("DELETE FROM " + MapSymbol.class.getAnnotation(Entity.class).name()).executeUpdate();
        int i = 0;
        while(i++ < 10){
            MapSymbol symbol = new MapSymbol();
            symbol.setName("테스트");
            symbol.setCode("SYMBOL" + i);
            symbol.setType("POLYGON");
            entityManager.persist(symbol);
        }
    }
}
