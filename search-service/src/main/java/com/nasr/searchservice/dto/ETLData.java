package com.nasr.searchservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ETLData {

    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "body")
    private String body;
    @JsonProperty(value = "url")
    private String url;
    @JsonProperty(value = "siteName")
    private String siteName;
    @JsonProperty(value = "iconUrl")
    private String iconUrl;


}
