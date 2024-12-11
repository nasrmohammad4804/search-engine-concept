package com.nasr.searchservice.enumeration;

import lombok.Getter;

import java.util.List;

@Getter
public enum SuggestionMatchQueriesType {
    PHRASE_PREFIX("phrase_prefix"), MATCH("match");

    private final String name;


     SuggestionMatchQueriesType(String name){
        this.name = name;

    }

    public static SuggestionMatchQueriesType read(String type) {

        return switch (type) {
            case "phrase_prefix":
                yield PHRASE_PREFIX;
            case "match":
                yield MATCH;

            default:
                throw new IllegalArgumentException("dont find any SuggestionMatchQueries by type : " + type);
        };
    }
    public static List<SuggestionMatchQueriesType> readAll(List<String> allTypes){

       return allTypes.stream().map(SuggestionMatchQueriesType::read)
                .toList();
    }
}
