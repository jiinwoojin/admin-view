package com.jiin.admin.website.view.repository;

import com.jiin.admin.entity.ProxyCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProxyCacheEntityRepository extends JpaRepository<ProxyCacheEntity, Long> {
    List<ProxyCacheEntity> findByNameIn(List<String> names);
}
