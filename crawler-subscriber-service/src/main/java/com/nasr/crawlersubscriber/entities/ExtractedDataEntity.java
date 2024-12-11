package com.nasr.crawlersubscriber.entities;

import lombok.*;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

import static com.nasr.crawlersubscriber.entities.ExtractedDataEntity.*;

@Document(value = COLLECTION_NAME )
@Setter
@Getter
public class ExtractedDataEntity {

    public static final String COLLECTION_NAME="extracted-web-pages";

    @Id
    private String id;
    private String url;
    private String title;
    private int responseStatus;
    private String LastModifiedResponseHeader;
    private String content;
    private String siteName;
    private String iconUrl;
    private Set<String> links;

    private ExtractedDataEntity(Builder builder) {
        this.id = builder.id;
        this.url = builder.url;
        this.title = builder.title;
        this.responseStatus = builder.responseStatus;
        LastModifiedResponseHeader = builder.lastModifiedResponseHeader;
        this.content = builder.content;
        this.links = builder.links;
        this.iconUrl=builder.iconUrl;
        this.siteName=builder.siteName;
    }

    public static class Builder{

        private final String id;
        private String url;
        private String title;
        private int responseStatus;
        private String lastModifiedResponseHeader;
        private String content;
        private String siteName;
        private String iconUrl;
        private Set<String> links = new HashSet<>();


        private Builder(){
            this.id= UUID.randomUUID().toString();
        }


        public static Builder builder(){
            return new Builder();
        }

        public Builder url(String url){
            this.url= url;
            return this;
        }
        public Builder title(String title){
            this.title= title;
            return this;
        }
        public Builder siteName(String siteName){
            this.siteName= siteName;
            return this;
        }
        public Builder iconUrl(String iconUrl){
            this.iconUrl= iconUrl;
            return this;
        }
        public Builder responseStatus(int responseStatus){
            this.responseStatus=responseStatus;
            return this;
        }
        public Builder lastModifiedResponseHeader(String lastModifiedResponseHeader){
            this.lastModifiedResponseHeader=lastModifiedResponseHeader;
            return this;
        }
        public Builder content(String content){
            this.content=content;
            return this;
        }
        public Builder links(Set<String> links){
            this.links=links;
            return this;
        }
        public ExtractedDataEntity build(){
            return new ExtractedDataEntity(this);
        }
    }
}
