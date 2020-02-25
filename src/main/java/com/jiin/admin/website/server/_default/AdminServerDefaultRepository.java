package com.jiin.admin.website.server._default;

import com.jiin.admin.entity.MapSymbol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminServerDefaultRepository extends CrudRepository<MapSymbol, String> {

    @Override
    Iterable<MapSymbol> findAll();
}
