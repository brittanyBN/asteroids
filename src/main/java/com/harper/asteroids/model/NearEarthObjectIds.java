package com.harper.asteroids.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NearEarthObjectIds {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
