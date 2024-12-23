package com.nasr.searchservice.constant;

public class WebpageSettingTemplate {

    public static final String SETTING = """
                {
                    "analysis": {
                      "analyzer": {
                        "custom_standard_analyzer": {
                          "tokenizer": "standard",
                          "filter": ["lowercase","stop"]
                        }
                      }
                    }
                  }
            """;
}
