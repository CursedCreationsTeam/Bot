package com.jagrosh.jmusicbot.commands.minecraft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Version {
    private String id;
    private String type;
    private String url;
    private String time;
    private String releaseTime;
    private String sha1;
    private int complianceLevel;

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
        this.type = type;
        this.url = url;
        this.time = time;
        this.releaseTime = releaseTime;
        this.sha1 = sha1;
        this.complianceLevel = complianceLevel;
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
