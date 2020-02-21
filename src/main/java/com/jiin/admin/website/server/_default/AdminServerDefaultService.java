package com.jiin.admin.website.server._default;

import com.jiin.admin.entity.BaseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class AdminServerDefaultService {

	@Resource
	private AdminServerDefaultMapper mapper;

	@Resource
	private AdminServerDefaultRepository repo;

	public Iterable<BaseEntity> test() {
		return repo.findAll();
	}
}
