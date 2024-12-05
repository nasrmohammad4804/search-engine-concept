package com.nasr.searchservice.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.nasr.searchservice.constant.ApplicationConstant.SPLITTING_SENTENCE_PATTERN;
import static com.nasr.searchservice.constant.ApplicationConstant.WHITE_SPACE_PATTERN;

public class RelatedTextExtraction {

    public static final int MAX_DISTANCE = 2;

    private static final LevenshteinDistance distance = new LevenshteinDistance();

    public static String filterTwoSentencesWithFuzzyOrderedQuery(String text, List<String> queryTerms) {

        String[] sentences = text.split(SPLITTING_SENTENCE_PATTERN);


        List<String> relevantSentencePairs = new ArrayList<>();
        for (int i = 0; i < sentences.length - 1; i++) {

            String combinedSentences = sentences[i] + " " + sentences[i + 1];
            if (containsFuzzyOrderedQuery(combinedSentences, queryTerms)) {
                relevantSentencePairs.add(combinedSentences);
            }
        }

        return relevantSentencePairs.stream().min(Comparator.comparingInt(String::length)).orElse("");
    }

    private static boolean containsFuzzyOrderedQuery(String text, List<String> queryTerms) {
        String[] words = text.split(WHITE_SPACE_PATTERN);
        int termIndex = 0;


        for (String word : words) {
            if (distance.apply(word.toLowerCase(), queryTerms.get(termIndex).toLowerCase()) <= MAX_DISTANCE) {
                termIndex++;
                if (termIndex == queryTerms.size()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getPhrasePrefixOrderedQueryByCheckQueryTermsWithHighlightText(String text, String[] queryTerms) {

        text = text.toLowerCase();
        queryTerms = convertQueryTermsToLowerCase(queryTerms);

        StringBuilder result = new StringBuilder();

        int finalTermQueryIndex = queryTerms.length - 1;
        String[] words = text.split(WHITE_SPACE_PATTERN);
        int termIndex = 0;

        for (String word : words) {

            if (termIndex == finalTermQueryIndex)
                termIndex = getTermIndexByCheckWordContainTermQuery(queryTerms, word, termIndex, result);

            else termIndex = getTermIndexByCheckWordEqualWithTermQuery(queryTerms, word, termIndex, result);

            if (termIndex == queryTerms.length)
                break;

        }

        return result.toString().trim();

    }


    public static String getMatchOrderedQueryByCheckQueryTermsWithHighlightText(List<String> highlightText, String[] queryTerms) {

        highlightText = convertStringOfListToLowerCase(highlightText);
        queryTerms = convertQueryTermsToLowerCase(queryTerms);

        StringBuilder result = new StringBuilder();
        int finalTermQueryIndex = queryTerms.length - 1;
        int termIndex = 0;

        for (String word : highlightText) {

            if (termIndex == finalTermQueryIndex)
                termIndex = getTermIndexByCheckWordContainTermQuery(queryTerms, word, termIndex, result);

            else termIndex = getTermIndexByCheckWordSimilarToTermQuery(queryTerms, word, termIndex, result);

            if (termIndex == queryTerms.length)
                break;
        }

        return result.toString().trim();
    }

    private static List<String> convertStringOfListToLowerCase(List<String> highlightText) {
        return highlightText.stream().map(String::toLowerCase).toList();
    }

    private static int getTermIndexByCheckWordSimilarToTermQuery(String[] queryTerms, String word, int termIndex, StringBuilder result) {
        boolean isSimilar = distance.apply(word, queryTerms[termIndex]) <= MAX_DISTANCE;

        if (isSimilar) {
            termIndex++;
            result.append(word).append(" ");
        }
        return termIndex;
    }

    private static int getTermIndexByCheckWordEqualWithTermQuery(String[] queryTerms, String word, int termIndex, StringBuilder result) {
        boolean isEqual = word.equals(queryTerms[termIndex]);
        if (isEqual) {
            termIndex++;
            result.append(word).append(" ");
        }
        return termIndex;
    }

    private static int getTermIndexByCheckWordContainTermQuery(String[] queryTerms, String word, int termIndex, StringBuilder result) {
        boolean contains = word.contains(queryTerms[termIndex]);

        if (contains) {
            termIndex++;
            result.append(word).append(" ");
        }
        return termIndex;
    }

    private static String[] convertQueryTermsToLowerCase(String[] queryTerms) {
        return Arrays.stream(queryTerms).map(String::toLowerCase)
                .toArray(String[]::new);
    }
}

