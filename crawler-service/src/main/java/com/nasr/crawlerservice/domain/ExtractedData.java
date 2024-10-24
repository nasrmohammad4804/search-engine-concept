package com.nasr.crawlerservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Set<String> links = new HashSet<>();
}
