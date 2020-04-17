package com.jiin.admin.website.view.repository;

import com.jiin.admin.entity.ProxyLayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProxyLayerEntityRepository extends JpaRepository<ProxyLayerEntity, Long> {
}
