package com.nasr.searchservice.dto;

import com.nasr.searchservice.enumeration.SuggestionMatchQueriesType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestData {

    private String id;
    private List<SuggestionMatchQueriesType> matchedQueries;
    private List<String> highlightQueries;
}
