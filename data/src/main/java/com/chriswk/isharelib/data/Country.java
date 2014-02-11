package com.chriswk.isharelib.data;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Country {

    @GraphId
    Long id;

    @Indexed(unique=true)
    String iso;

    String name;

    public Country() {
    }

    public Country(String iso, String name) {
        this.iso = iso;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
