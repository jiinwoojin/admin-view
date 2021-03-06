package com.jiin.admin.config;

import com.jiin.admin.servlet.AdminViewServlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Controller
public class RootController {
    @RequestMapping(value="/", method = RequestMethod.GET)
    public void welcome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/" + AdminViewServlet.CONTEXT_PATH + "/home/dashboard");
    }
}
