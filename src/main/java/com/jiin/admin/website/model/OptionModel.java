package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionModel {
    private String label;
    private long value;

    public OptionModel(){

    }

    public OptionModel(String label, long value){
        this.label = label;
        this.value = value;
    }
}
