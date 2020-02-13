package com.jiin.admin.website.view;

import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.servlet.AdminViewServlet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class AdminViewController {
	@RequestMapping(value = AdminViewServlet.WELCOME_PATH, method = { RequestMethod.GET })
	public String welcome(ModelMap model, HttpServletRequest request,
							 HttpServletResponse reponse) {

		return "welcome";
	}

}
