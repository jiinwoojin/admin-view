package com.jiin.admin.website.server._default;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class AdminServerDefaultService {

	@Resource
	private AdminServerDefaultMapper mapper;

	public List<Integer> test() {
		return mapper.testByXml();
	}
}
