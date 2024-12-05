package com.nasr.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.dto.SuggestData;
import com.nasr.searchservice.entities.WebpageEntity;
import com.nasr.searchservice.repository.WebpageRepository;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nasr.searchservice.constant.ApplicationConstant.*;
import static com.nasr.searchservice.entities.WebpageEntity.INDEX_NAME;
import static com.nasr.searchservice.enumeration.SuggestionMatchQueriesType.*;
import static com.nasr.searchservice.util.RelatedTextExtraction.getMatchOrderedQueryByCheckQueryTermsWithHighlightText;
import static com.nasr.searchservice.util.RelatedTextExtraction.getPhrasePrefixOrderedQueryByCheckQueryTermsWithHighlightText;

@Service
public class WebpageServiceImpl implements WebpageService {

    @Autowired
    private WebpageRepository repository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @Override
    public WebpageEntity save(ETLData etlWebpageData) {

        WebpageEntity entity = convertDTOToEntity(etlWebpageData);
        return repository.save(entity);
    }

    @Override
    public List<String> suggest(String query) throws IOException {


        SearchResponse<WebpageEntity> response = elasticsearchClient
                .search(
                        SearchRequest.of(builder -> builder.index(INDEX_NAME)
                                .source(b -> b.fetch(false))
                                .query(
                                        q -> q.bool(bool -> bool.should(
                                                List.of(
                                                        getMatchPhrasePrefixSuggestionQuery(query),
                                                        getMultiMatchSuggestionQuery(query)
                                                )
                                        ).minimumShouldMatch(String.valueOf(1))))
                                .size(SUGGESTION_QUERY_SIZE)
                                .highlight(b -> b.fields("title", getSuggestionQueryHighlight())))
                        , WebpageEntity.class);

        List<SuggestData> suggestData = extractSuggestDataFromResponse(response);
        return getMostRelativeQuerySuggestion(query, suggestData);

    }

    @Override
    public Page<WebpageEntity> search(String query, Pageable pageable){
        return null;
    }

    private List<String> getMostRelativeQuerySuggestion(String query, List<SuggestData> suggestData) {

        Pattern pattern = Pattern.compile(HIGHLIGHT_DATA_PATTERN);
        String[] terms = query.split(WHITE_SPACE_PATTERN);

        Set<String> querySuggestions = new HashSet<>();
        suggestData.forEach(data -> {

            String highlight = data.getHighlightQueries().getFirst();
            Matcher matcher = pattern.matcher(highlight);
            checkHighlightResultMatchedWithTermQueries(matcher, terms, querySuggestions, data.getMatchedQueries().contains(PHRASE_PREFIX));
        });

        return querySuggestions.stream()
                .limit(SUGGESTION_RESULT_SIZE)
                .toList();
    }

    private static void checkHighlightResultMatchedWithTermQueries(Matcher matcher, String[] terms, Set<String> querySuggestions, boolean isPhrasePrefix) {

        if (isPhrasePrefix) {

            if (matcher.find()) {
                String highlightText = matcher.group(1);
                String suggestion = getPhrasePrefixOrderedQueryByCheckQueryTermsWithHighlightText(highlightText, terms);
                querySuggestions.add(suggestion);
            }
            return;
        }

        List<String> highlightTexts = new ArrayList<>();

        while (matcher.find()) {
            highlightTexts.add(matcher.group(1));
        }
        String suggestion = getMatchOrderedQueryByCheckQueryTermsWithHighlightText(highlightTexts, terms);
        querySuggestions.add(suggestion);
    }


    private List<SuggestData> extractSuggestDataFromResponse(SearchResponse<WebpageEntity> response) {

        return response.hits()
                .hits()
                .stream()
                .map(hit -> SuggestData.builder()
                        .id(hit.id())
                        .matchedQueries(readAll(hit.matchedQueries()))
                        .highlightQueries(hit.highlight().get("title"))
                        .build()
                )
                .toList();
    }


    private Query getMatchPhrasePrefixSuggestionQuery(String query) {

        return Query.of(b -> b.matchPhrasePrefix(p -> p.field("title").query(query)
                .slop(3)
                .boost(2F)
                .queryName(PHRASE_PREFIX.getName()))
        );
    }

    private Query getMultiMatchSuggestionQuery(String query) {
        return Query.of(b -> b.multiMatch(m ->
                m.fields(List.of("title"))
                        .query(query)
                        .operator(Operator.And)
                        .fuzziness(String.valueOf(2))
                        .boost(1F)
                        .queryName(MATCH.getName())
                        .type(TextQueryType.BoolPrefix)
        ));
    }

    private HighlightField getSuggestionQueryHighlight() {
        return HighlightField.of(hBuilder -> hBuilder.preTags("<em>")
                .postTags("</em>")
                .numberOfFragments(1)
                .fragmentSize(150));
    }

    private WebpageEntity convertDTOToEntity(ETLData data) {
        return new WebpageEntity(data.getId(), data.getTitle(), data.getUrl(), data.getBody());
    }
}
