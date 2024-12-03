package com.nasr.searchservice.constant;

public class WebpageSettingTemplate {

    public static final String SETTING = """
                {
                    "number_of_replicas": 2,
                    "number_of_shards": 2,
                    "analysis": {
                      "tokenizer": {
                        "edge_ngram_tokenizer": {
                          "type": "edge_ngram",
                          "min_gram": 1,
                          "max_gram": 25,
                          "token_chars": ["letter", "digit"]
                        }
                      },
                      "analyzer": {
                        "edge_ngram_analyzer": {
                          "type": "custom",
                          "tokenizer": "edge_ngram_tokenizer"
                        }
                      }
                    }
                  }
            """;
}
