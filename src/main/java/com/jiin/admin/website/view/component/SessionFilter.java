package com.jiin.admin.website.view.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class SessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Cookie session = Arrays.stream(cookies)
                                    .filter(o -> o.getName().equals("JSESSIONID"))
                                    .findFirst()
                                    .orElse(null);
            if (session != null) {
                session.setHttpOnly(true);
                session.setSecure(true);
                res.addCookie(session);
            }

            try {
                chain.doFilter(req, res);
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            } catch (ServletException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }
    }
}
