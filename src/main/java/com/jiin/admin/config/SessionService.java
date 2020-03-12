package com.jiin.admin.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jiin.admin.Constants;
import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


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
