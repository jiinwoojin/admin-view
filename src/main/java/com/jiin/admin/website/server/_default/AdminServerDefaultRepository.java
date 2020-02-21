package com.jiin.admin.website.server._default;

import com.jiin.admin.entity.BaseEntity;
import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminServerDefaultRepository extends CrudRepository<BaseEntity, String> {

    @Override
    Iterable<BaseEntity> findAll();
}
