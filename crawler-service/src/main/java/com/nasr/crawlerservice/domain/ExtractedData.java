package com.nasr.crawlerservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtractedData {

    private String url;
    private String title;
    private String content;
    private String siteName;
    private String iconUrl;
    private int responseStatus;
    private String lastModifiedResponseHeader;
    private Set<String> links = new HashSet<>();
}
