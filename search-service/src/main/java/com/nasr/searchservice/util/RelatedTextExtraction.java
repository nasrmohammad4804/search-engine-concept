package com.nasr.searchservice.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RelatedTextExtraction {

    private static final int MAX_DISTANCE = 2;
    private  static  final String SENTENCE_PATTERN="(?<=[.!?])\\s+";
    private static final LevenshteinDistance distance = new LevenshteinDistance();

    public static String filterTwoSentencesWithFuzzyOrderedQuery(String text, List<String> queryTerms) {

        String[] sentences = text.split(SENTENCE_PATTERN);


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
        String[] words = text.split("\\s+");
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
}
