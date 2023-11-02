package com.jagrosh.jmusicbot.commands.minecraft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Version {
    private final String id;
    private final String url;

    @JsonCreator
    public Version(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("url") String url,
            @JsonProperty("time") String time,
            @JsonProperty("releaseTime") String releaseTime,
            @JsonProperty("sha1") String sha1,
            @JsonProperty("complianceLevel") int complianceLevel) {
        this.id = id;
        this.url = url;
    }

    // Add getters and setters if needed
    public String getId()
    {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
