package com.jiin.admin.website.server.controller;

import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.website.server.vo.Center;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class ServerCheckController {
	@RequestMapping(value = "server-state", method = { RequestMethod.GET })
	public List<Center> state(ModelMap model, HttpServletRequest request,
							 HttpServletResponse reponse) {
		List<Center> centers = new ArrayList();
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		return centers;
	}

	@RequestMapping(value = "server-sync", method = { RequestMethod.GET })
	public List<Center> sync(ModelMap model, HttpServletRequest request,
							HttpServletResponse reponse) {
		List<Center> centers = new ArrayList();
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		return centers;
	}

	@RequestMapping(value = "server-data", method = { RequestMethod.GET })
	public List<Center> data(ModelMap model, HttpServletRequest request,
						 HttpServletResponse reponse) {
		List<Center> centers = new ArrayList();
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		centers.add(new Center());
		return centers;
	}
}
