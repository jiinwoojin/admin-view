package com.jiin.admin.website.server._default;

import com.jiin.admin.entity.BaseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class AdminServerDefaultController {

	@Resource
	private AdminServerDefaultService service;

	@RequestMapping(value = "server-state", method = { RequestMethod.GET })
	public Iterable<BaseEntity> test() {
		return service.test();
	}

}
