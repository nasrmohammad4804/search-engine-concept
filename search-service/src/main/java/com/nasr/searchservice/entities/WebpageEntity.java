package com.nasr.searchservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.nasr.searchservice.entities.WebpageEntity.INDEX_NAME;

@Data
@Builder
@Document(indexName = INDEX_NAME, createIndex = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebpageEntity {

    public static final String INDEX_NAME = "webpages";

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "custom_standard_analyzer")
    private String title;

    @Field(type = FieldType.Text)
    private String url;

    @Field(type = FieldType.Text, analyzer = "custom_standard_analyzer")
    private String body;

    @Field(type = FieldType.Text)
    private String iconUrl;

    @Field(type = FieldType.Text)
    private String siteName;

    @Field(type = FieldType.Date,format = DateFormat.date_time)
    private Date createdDate;
}
