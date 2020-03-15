package com.jiin.admin.config;


import com.jiin.admin.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Service
public class SessionService {

    @Resource
    private HttpSession session;

    /**
     * set
     * @param message
     */
    public void message(String message) {
        session.setAttribute(Constants.SESSION_MESSAGE,message);
    }

    /**
     * get
     * @return
     */
    public String message() {
        String message = (String) session.getAttribute(Constants.SESSION_MESSAGE);
        session.removeAttribute(Constants.SESSION_MESSAGE);
        return message;
    }
}
