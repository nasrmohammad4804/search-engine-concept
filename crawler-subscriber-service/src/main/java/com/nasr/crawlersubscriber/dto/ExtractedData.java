package com.nasr.crawlersubscriber.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedData {

    private String url;
    private String title;
    private int responseStatus;
    private String lastModifiedResponseHeader;
    private String content;
    private String siteName;
    private String iconUrl;
    private Set<String> links = new HashSet<>();
}
