package com.nasr.searchservice.constant;

public class ApplicationConstant {

    public static final String ETL_DATA_TOPIC_NAME = "etl_data";
    public  static final String ETL_DATA_GROUP_ID="etl_data_group";

    public static final int SUGGESTION_QUERY_SIZE=1000;
    public static final int SUGGESTION_RESULT_SIZE=10;

    public static final String HIGHLIGHT_DATA_PATTERN ="<em>(.*?)</em>";
    public static final String WHITE_SPACE_PATTERN = "\\s+";

    public static final String SPLITTING_SENTENCE_PATTERN = "(?<=[.!?])\\s+";
}
