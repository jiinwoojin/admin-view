package com.jiin.admin.converter.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

public class GssContainer {
    @XmlAttribute
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
