package com.jiin.admin.servlet;

import com.jiin.admin.website.server.AdminServerConfig;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


@Configuration
public class AdminServerServlet {
	public static final String CONTEXT_PATH = "server";
	public static final String EXTENSION = "jiin";
	public static final String WELCOME_PATH = "welcome." + EXTENSION;
	@Bean
	public ServletRegistrationBean servletRegistration() {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AdminServerConfig.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setApplicationContext(applicationContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
		final ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet, "/"+AdminServerServlet.CONTEXT_PATH+"/*");
		registration.setName(AdminServerServlet.CONTEXT_PATH);
		return registration;
	}
}
