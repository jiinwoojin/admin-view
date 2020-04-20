package com.jiin.admin.website.view.repository;

import com.jiin.admin.entity.ProxyCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProxyCacheEntityRepository extends JpaRepository<ProxyCacheEntity, Long> {
    Optional<ProxyCacheEntity> findByName(String name);
    List<ProxyCacheEntity> findByNameIn(List<String> names);
    List<ProxyCacheEntity> findBySelectedIsTrue();
}
