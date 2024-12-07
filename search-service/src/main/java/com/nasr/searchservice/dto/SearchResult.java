package com.nasr.searchservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResult {

    private int totalPage;
    private int pageNumber;
    private int pageSize;

    private List<SearchData> searchData = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public final static class SearchData {

        private String logoUrl;
        private String siteName;
        private String title;
        private String url;
        private String bodySummarize;
    }
}
