package com.nasr.searchservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResult {

    private MetaData metaData;
    private List<SearchData> searchData = new ArrayList<>();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public final static class MetaData{
        private int totalPage;
        private long totalRecords;
        private int responseTime;
        private int pageNumber;
        private int pageSize;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public final static class SearchData {

        private String iconUrl;
        private String siteName;
        private String title;
        private String url;
        private String bodySummarize;
    }
}
