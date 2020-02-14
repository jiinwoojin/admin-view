package com.jiin.admin.servlet;

import com.jiin.admin.website.view.AdminViewConfig;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


@Configuration
public class AdminViewServlet {
	public static final String CONTEXT_PATH = "view";
	public static final String EXTENSION = "jiin";
	public static final String WELCOME_PATH = "welcome." + EXTENSION;
	@Bean
	public ServletRegistrationBean viewServletRegistration() {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AdminViewConfig.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setApplicationContext(applicationContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
		final ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet, "/"+AdminViewServlet.CONTEXT_PATH+"/*");
		registration.setName(AdminViewServlet.CONTEXT_PATH);
		return registration;
	}
}
