package com.jiin.admin.starter;

import com.jiin.admin.servlet.AdminViewServlet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping(value="/")
public class RootController {
    @RequestMapping(method = RequestMethod.GET)
    public void welcome(HttpServletResponse response) throws IOException {
        response.sendRedirect(AdminViewServlet.CONTEXT_PATH + "/" + AdminViewServlet.WELCOME_PATH);
    }
}
