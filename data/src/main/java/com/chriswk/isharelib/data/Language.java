package com.chriswk.isharelib.data;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Language {
    @GraphId
    Long id;
	
    String isocode;

    String name;

    public Language() {
    }

    public Language(String isocode, String name) {
        this.isocode = isocode;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getIsocode() {
        return isocode;
    }

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
