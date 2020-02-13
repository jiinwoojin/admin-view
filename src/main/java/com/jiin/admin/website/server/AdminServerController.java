package com.jiin.admin.website.server;

import com.jiin.admin.servlet.AdminServerServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@RestController
public class AdminServerController {
	@RequestMapping(value = AdminServerServlet.WELCOME_PATH, method = { RequestMethod.GET })
	public String welcome(ModelMap model, HttpServletRequest request,
							 HttpServletResponse reponse) {

		return "welcomdde";
	}

}
