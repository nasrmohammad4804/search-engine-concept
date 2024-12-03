package com.nasr.searchservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static com.nasr.searchservice.entities.WebpageEntity.*;

@Data
@Document(indexName = INDEX_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebpageEntity {

    public static final String INDEX_NAME="webpages";

    @Id
    private String id;

    @Field(type = FieldType.Text,analyzer = "edge_ngram_analyzer")
    private String title;

    @Field(type = FieldType.Text)
    private String url;

    @Field(type = FieldType.Text)
    private String body;

}
