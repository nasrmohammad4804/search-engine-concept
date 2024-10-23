package com.nasr.crawlerservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedData {

    private String url;
    private String title;
    private int responseStatus;
    private String lastModifiedResponseHeader;
    private String content;
    private List<String> links = new ArrayList<>();
}
