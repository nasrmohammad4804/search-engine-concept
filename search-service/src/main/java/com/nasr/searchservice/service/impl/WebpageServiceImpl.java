package com.nasr.searchservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.nasr.searchservice.dto.ETLData;
import com.nasr.searchservice.dto.SearchResult;
import com.nasr.searchservice.dto.SuggestData;
import com.nasr.searchservice.entities.WebpageEntity;
import com.nasr.searchservice.enumeration.HighlightFieldType;
import com.nasr.searchservice.external.EmbeddingExternalService;
import com.nasr.searchservice.repository.WebpageRepository;
import com.nasr.searchservice.service.WebpageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.elastic.clients.elasticsearch._types.query_dsl.Operator.And;
import static com.nasr.searchservice.constant.ApplicationConstant.*;
import static com.nasr.searchservice.entities.WebpageEntity.INDEX_NAME;
import static com.nasr.searchservice.enumeration.HighlightFieldType.BODY;
import static com.nasr.searchservice.enumeration.HighlightFieldType.TITLE;
import static com.nasr.searchservice.enumeration.SuggestionMatchQueriesType.*;
import static com.nasr.searchservice.util.RelatedTextExtraction.getMatchOrderedQueryByCheckQueryTermsWithHighlightText;
import static com.nasr.searchservice.util.RelatedTextExtraction.getPhrasePrefixOrderedQueryByCheckQueryTermsWithHighlightText;

@Service
public class WebpageServiceImpl implements WebpageService {

    @Autowired
    private WebpageRepository repository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private EmbeddingExternalService embeddingExternalService;


    @Override
    public WebpageEntity save(ETLData etlWebpageData) {

        WebpageEntity entity = convertDTOToEntity(etlWebpageData);
        return repository.save(entity);
    }

    @Override
    public Iterable<WebpageEntity> saveAll(List<ETLData> etlWebpageData) {

        List<WebpageEntity> webpages = etlWebpageData.stream().map(this::convertDTOToEntity)
                .toList();

        return repository.saveAll(webpages);
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
                                .highlight(b -> b.fields("title", getHighlight(TITLE))))
                        , WebpageEntity.class);

        List<SuggestData> suggestData = extractSuggestDataFromResponse(response);
        return getMostRelativeQuerySuggestion(query, suggestData);

    }

    @Override
    public SearchResult search(String query, Pageable pageable) throws IOException {

        long queryStartTime = System.currentTimeMillis();

        SearchResponse<WebpageEntity> response = elasticsearchClient.search(SearchRequest.of(builder -> builder.index(INDEX_NAME)
                .trackTotalHits(b -> b.enabled(true))
                .size(pageable.getPageSize())
                .from((pageable.getPageNumber() - 1) * pageable.getPageSize())
                .query(q ->
                        q.bool(bool ->
                                bool.should(
                                                List.of(getTitleSearchQuery(query), getBodySearchQuery(query))
                                        )
                                        .minimumShouldMatch(String.valueOf(1))
                        )
                ).highlight(b -> b.fields("body", getHighlight(BODY)))
        ), WebpageEntity.class);


        SearchResult searchResult = extractSearchFromResponse(response, pageable);
        searchResult.getMetaData().setResponseTime((int) (System.currentTimeMillis() - queryStartTime));
        return searchResult;
    }

    @Override
    public SearchResult embedSearch(String query, Pageable pageable) throws IOException {

        List<Float> embeddingData = embeddingExternalService.getEmbeddingData(query);


        SearchResponse<WebpageEntity> response = elasticsearchClient.search(SearchRequest.of(builder -> builder.index(INDEX_NAME)
                .trackTotalHits(b -> b.enabled(true))
                .size(pageable.getPageSize())
                .from((pageable.getPageNumber() - 1) * pageable.getPageSize())

                .knn(knnBuilder -> knnBuilder.queryVector(embeddingData).field("dimensions")
                        .k(pageable.getPageSize())
                        .numCandidates((long) Math.pow(pageable.getPageSize(), 2)))

        ), WebpageEntity.class);


        return extractSearchFromResponse(response, pageable);
    }

    private Query getTitleSearchQuery(String query) {

        return Query.of(b -> b.match(m -> m.field("title")
                .query(query)
                .boost(5F)
                .operator(And)
                .fuzziness(String.valueOf(2)
                )));
    }

    private Query getBodySearchQuery(String query) {
        return Query.of(queryBuilder -> queryBuilder.bool(b -> b.should(
                                List.of(
                                        Query.of(phraseQuery -> phraseQuery.matchPhrase(phraseBuilder ->
                                                phraseBuilder.field("body")
                                                        .query(query)
                                                        .slop(5)
                                                        .boost(2F))
                                        ),
                                        Query.of(matchQuery -> matchQuery.match(matchBuilder ->
                                                matchBuilder.field("body")
                                                        .query(query)
                                                        .fuzziness(String.valueOf(2))
                                                        .operator(And)
                                                        .boost(1F)
                                        ))
                                )
                        )
                        .minimumShouldMatch(String.valueOf(1))
        ));
    }

    private SearchResult extractSearchFromResponse(SearchResponse<WebpageEntity> response, Pageable pageable) {
        SearchResult searchResult = new SearchResult();

        assert response.hits().total() != null;
        long totalValue = response.hits().total().value();

        searchResult.setMetaData(SearchResult.MetaData.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(getTotalPage((int) totalValue, pageable.getPageSize()))
                .totalRecords(totalValue)
                .build());

        response.hits().hits().forEach(hit -> {

            WebpageEntity source = hit.source();
            String summary = extractBodySummaryFromHighlight(hit);

            assert source != null;

            searchResult.getSearchData().add(
                    SearchResult.SearchData
                            .builder()
                            .url(source.getUrl())
                            .bodySummarize(summary)
                            .title(source.getTitle())
                            .iconUrl(source.getIconUrl())
                            .siteName(source.getSiteName())
                            .build());
        });

        return searchResult;
    }


    private String extractBodySummaryFromHighlight(Hit<WebpageEntity> hit) {

        List<String> bodyHighlight = hit.highlight().get("body");
        assert hit.source() != null;
        String body = hit.source().getBody();
        String highlight = "";

        if (bodyHighlight != null && !bodyHighlight.isEmpty()) {
            highlight = bodyHighlight.getFirst();
        }
        Pattern pattern = Pattern.compile(HIGHLIGHT_DATA_PATTERN);
        Matcher matcher = pattern.matcher(highlight);

        int matchCount = 0;
        int firstGroupLength = 0;

        while (matcher.find()) {
            matchCount++;
            if (matchCount == 1)
                firstGroupLength = matcher.group(1).length();
        }
        int firstHighlightProcessingIndex = highlight.indexOf(HIGHLIGHT_PRE_TAG);

        if (matchCount == 1) {
            return extractMostRelativeSentenceForOneMatching(highlight, firstHighlightProcessingIndex, body, firstGroupLength);
        } else if (matchCount > 1) {
            return extractMostRelativeSentenceForMultipleMatching(highlight, firstHighlightProcessingIndex, body);
        }

        return body.substring(0, getSentenceEnd(body, 5));
    }

    private String extractMostRelativeSentenceForMultipleMatching(String highlight, int firstHighlightProcessingIndex, String body) {
        int lastHighlightProcessingIndex;
        lastHighlightProcessingIndex = highlight.lastIndexOf(HIGHLIGHT_POST_TAG);
        String processingHighlightText = highlight.substring(firstHighlightProcessingIndex, lastHighlightProcessingIndex);
        processingHighlightText = processingHighlightText.replaceAll("(<strong>)|(</strong>)", "");
        return extractFullSentence(body, processingHighlightText);
    }

    private static String extractMostRelativeSentenceForOneMatching(String highlight, int firstIndex, String body, int firstGroupLength) {


        String processingHighlightText = highlight.substring(firstIndex);
        processingHighlightText = processingHighlightText.replaceAll("(<strong>)|(</strong>)", "");
        int bodyStartIndex = body.indexOf(processingHighlightText);
        int sentenceStart = getSentenceStart(body, bodyStartIndex);
        int sentenceEnd = getSentenceEnd(body, bodyStartIndex + firstGroupLength);
        return body.substring(sentenceStart, sentenceEnd);
    }

    private String extractFullSentence(String text, String substring) {
        int startIndex = text.indexOf(substring);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Substring not found in text.");
        }
        int endIndex = startIndex + substring.length();

        int sentenceStart = getSentenceStart(text, startIndex);
        int sentenceEnd = getSentenceEnd(text, endIndex);

        if (sentenceEnd < text.length()) {
            sentenceEnd++;
        }

        return text.substring(sentenceStart, sentenceEnd).trim();
    }

    private static int getSentenceEnd(String text, int endIndex) {
        int sentenceEnd = endIndex;
        while (sentenceEnd < text.length() && !SYMBOLS_END_UP_SENTENCE.contains(text.charAt(sentenceEnd))) {
            sentenceEnd++;
        }
        return sentenceEnd;
    }

    private static int getSentenceStart(String text, int startIndex) {
        int sentenceStart = startIndex;
        while (sentenceStart > 0 && !SYMBOLS_END_UP_SENTENCE.contains(text.charAt(sentenceStart - 1))) {
            sentenceStart--;
        }
        return sentenceStart;
    }

    private int getTotalPage(int totalNumber, int size) {

        int totalPage = totalNumber / size;
        return (totalNumber % size == 0) ? totalPage : totalPage + 1;
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
                        .operator(And)
                        .fuzziness(String.valueOf(2))
                        .boost(1F)
                        .queryName(MATCH.getName())
                        .type(TextQueryType.BoolPrefix)
        ));
    }

    private HighlightField getHighlight(HighlightFieldType highlightFieldType) {
        return HighlightField.of(hBuilder -> hBuilder.preTags(HIGHLIGHT_PRE_TAG)
                .postTags(HIGHLIGHT_POST_TAG)
                .numberOfFragments(1)
                .fragmentSize(highlightFieldType.getFragmentSize()));
    }

    private WebpageEntity convertDTOToEntity(ETLData data) {

        return WebpageEntity.builder()
                .id(data.getId())
                .url(data.getUrl())
                .body(data.getBody())
                .title(data.getTitle())
                .iconUrl(data.getIconUrl())
                .siteName(data.getSiteName())
                .createdDate(Date.from(Instant.now()))
                .build();
    }
}
