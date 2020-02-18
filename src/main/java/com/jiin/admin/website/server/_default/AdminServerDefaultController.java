package com.jiin.admin.website.server._default;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
public class AdminServerDefaultController {

	@Resource
	private AdminServerDefaultService service;

	@RequestMapping(value = "server-state", method = { RequestMethod.GET })
	public List<Integer> test() {
		return service.test();
	}

}
