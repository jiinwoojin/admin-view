package com.jiin.admin.website.view.repository;

import com.jiin.admin.entity.ProxyLayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProxyLayerEntityRepository extends JpaRepository<ProxyLayerEntity, Long> {
    Optional<ProxyLayerEntity> findByName(String name);
    List<ProxyLayerEntity> findByNameIn(List<String> names);
    List<ProxyLayerEntity> findBySelectedIsTrue();
}
