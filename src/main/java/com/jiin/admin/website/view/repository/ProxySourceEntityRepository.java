package com.jiin.admin.website.view.repository;

import com.jiin.admin.entity.ProxySourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProxySourceEntityRepository extends JpaRepository<ProxySourceEntity, Long> {
    List<ProxySourceEntity> findByNameIn(List<String> names);
}
