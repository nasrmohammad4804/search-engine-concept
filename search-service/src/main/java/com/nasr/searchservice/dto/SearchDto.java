package com.nasr.searchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SearchDto {

    private String id;
    private String body;
    private String bodySummary;
    private Double score;
}
